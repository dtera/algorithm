//
// Created by 2024 dterazhao.
//

#include "heu/phe_kit.h"
#include "heu/ecc_factory.h"

const std::string PheKit::empty;
const std::string PheKit::ed25519 = "ed25519";
const std::string PheKit::curve25519 = "curve25519";
const std::string PheKit::sm2 = "sm2";
const std::string PheKit::secp256k1 = "secp256k1";
const std::string PheKit::secp192r1 = "secp192r1";
const std::string PheKit::secp256r1 = "secp256r1";
const std::string PheKit::fourq = "fourq";


//**************************************************PheKit Protect Begin************************************************
PheKit::PheKit(const yacl::ByteContainerView &pk_buffer, const std::unique_ptr<yacl::ByteContainerView> &sk_buffer,
               const int64_t scale, const int scale_cnt, const bool register_ec_lib) : scale_cnt_(scale_cnt) {
    if (register_ec_lib) {
        yacl::crypto::register_ec_lib();
    }
    if (sk_buffer == nullptr) {
        has_secret_key = false;
        dhe_kit_ = std::make_shared<heu::lib::phe::DestinationHeKit>(pk_buffer);
        init<heu::lib::phe::DestinationHeKit>(dhe_kit_, scale);
    } else {
        has_secret_key = true;
        he_kit_ = std::make_shared<heu::lib::phe::HeKit>(pk_buffer, *sk_buffer);

        init<heu::lib::phe::HeKit>(he_kit_, scale);
        decryptor_ = he_kit_->GetDecryptor();
    }
}

template<typename T>
void PheKit::init(const std::shared_ptr<T> &kit, int64_t scale) {
    encryptor_ = kit->GetEncryptor();
    evaluator_ = kit->GetEvaluator();

    if (kit->GetSchemaType() == SchemaType::ElGamal) {
        scale = 1e4;
    }
    for (int i = 1; i <= scale_cnt_; ++i) {
        encoders_.insert({
            i, std::make_shared<heu::lib::phe::PlainEncoder>(
                kit->template GetEncoder<heu::lib::phe::PlainEncoder>(std::pow(scale, i)))
        });
        batch_encoders_.insert({
            i, std::make_shared<heu::lib::phe::BatchEncoder>(
                kit->template GetEncoder<heu::lib::phe::BatchEncoder>(std::pow(scale, i)))
        });
    }

    encoder_f = [this](auto &&m) { return encoders_.at(1)->Encode(std::forward<decltype(m)>(m)); };
    decoder_f = [&](const Plaintext &pt, double *out, const int scale_cnt = 1) {
        if (encoders_.count(scale_cnt) == 0) {
            throw std::invalid_argument("have no plain encoder for scale_cnt: " + std::to_string(scale_cnt));
        }
        *out = encoders_.at(scale_cnt)->Decode<double>(pt);
    };
    batch_encoder_f = [this](auto &&m1, auto &&m2) {
        return batch_encoders_.at(1)->Encode(std::forward<decltype(m1)>(m1), std::forward<decltype(m2)>(m2));
    };
    batch_decoder_f = [&](const Plaintext &pt, double *out, const int scale_cnt = 1) {
        if (batch_encoders_.count(scale_cnt) == 0) {
            throw std::invalid_argument("have no batch encoder for scale_cnt: " + std::to_string(scale_cnt));
        }
        out[0] = batch_encoders_.at(scale_cnt)->Decode<double, 0>(pt);
        out[1] = batch_encoders_.at(scale_cnt)->Decode<double, 1>(pt);
    };

    add_f = [&](const Ciphertext *res, const Ciphertext &b) { evaluator_->AddInplace(res->data(), b.c_data()); };
    sub_f = [&](const Ciphertext *res, const Ciphertext &b) { evaluator_->SubInplace(res->data(), b.c_data()); };
    add_p_f = [&](const Ciphertext *res, const Plaintext &b) { evaluator_->AddInplace(res->data(), b); };
    sub_p_f = [&](const Ciphertext *res, const Plaintext &b) { evaluator_->SubInplace(res->data(), b); };
    mul_p_f = [&](const Ciphertext *res, const Plaintext &b) { evaluator_->MulInplace(res->data(), b); };

    zero = encryptor_->EncryptZero();
}

std::unique_ptr<yacl::ByteContainerView> PheKit::getBuffer(const std::string &s_buffer) {
    return s_buffer.empty() ? nullptr : std::make_unique<yacl::ByteContainerView>(s_buffer.data(), s_buffer.size());
}

bool PheKit::hasSecretKey() const { return has_secret_key; }

const std::shared_ptr<heu::lib::phe::PublicKey> &PheKit::getPublicKey() const {
    if (has_secret_key) {
        return he_kit_->GetPublicKey();
    }
    return dhe_kit_->GetPublicKey();
}

const std::shared_ptr<heu::lib::phe::SecretKey> &PheKit::getSecretKey() const {
    if (has_secret_key) {
        return he_kit_->GetSecretKey();
    }
    throw std::invalid_argument("have no secret key");
}

template<typename... ARGS>
Ciphertext *PheKit::encrypt(std::function<Plaintext(ARGS...)> encoder, ARGS... args) {
    return new Ciphertext(encryptor_->Encrypt(encoder(std::forward<ARGS>(args)...)));
}

Ciphertext *PheKit::encrypts(const size_t size, const std::function<void(int, Ciphertext *)> &do_encrypt,
                             const std::string &mark, const int repeats) {
    sw.Mark(mark);
    const auto res = new Ciphertext[size * repeats];
    ParallelFor(size, [&](const int i) {
        do_encrypt(i, res);
    });
    sw.Stop();

    return res;
}

Ciphertext *PheKit::encrypts(const size_t size, const std::function<Plaintext(int)> &encoder,
                             const std::string &mark) {
    return encrypts(size, [&](const int i, Ciphertext *res) {
        res[i] = encryptor_->Encrypt(encoder(i));
    }, mark);
}

Ciphertext *PheKit::encryptPairUnpack(const double m1, const double m2) const {
    const auto res = new Ciphertext[2];
    res[0] = encryptor_->Encrypt(encoder_f(m1));
    res[1] = encryptor_->Encrypt(encoder_f(m2));
    return res;
}

Ciphertext *PheKit::encryptPairsUnpack(const double *ms1, const double *ms2, const size_t size,
                                       const std::string &mark) {
    return encrypts(size, [&](const int i, Ciphertext *res) {
        res[i] = encryptor_->Encrypt(encoder_f(ms1[i]));
        res[i + size] = encryptor_->Encrypt(encoder_f(ms2[i]));
    }, mark, 2);
}

template<typename T>
void PheKit::decrypt(const Ciphertext &ct, T *out, std::function<void(const Plaintext &, T *, int)> decoder) {
    Plaintext pt;
    decryptor_->Decrypt(ct.c_data(), &pt);
    decoder(pt, out, ct.scale_cnt());
}

template<typename T>
void PheKit::decrypts(const Ciphertext *cts,
                      size_t size,
                      T *out,
                      std::function<void(const Plaintext &, T *, int)> decoder,
                      const std::string &mark) {
    sw.Mark(mark);
    ParallelFor(size, [&](int i) {
        this->decrypt(cts[i], &out[i], decoder);
    });
    sw.Stop();
}

template<typename T, int rescale_updater>
Ciphertext *PheKit::op(const Ciphertext &a,
                       const T &b,
                       const std::function<void(Ciphertext *, const T &)> &op_f) {
    const auto res = new Ciphertext();
    res->copy_from(a);
    op_f(res, b);
    res->rescale(rescale_updater);
    return res;
}

template<typename T, int rescale_updater>
Ciphertext *PheKit::op(const Ciphertext *a,
                       const T *b,
                       const size_t size,
                       const std::function<void(Ciphertext *, const T &)> &
                       op_f,
                       const std::string &mark) {
    sw.Mark(mark);
    auto *res = new Ciphertext[size];
    ParallelFor(size, [&](const int i) {
        res[i].copy_from(a[i]);
        opInplace<T, rescale_updater>(&res[i], b[i], op_f);
    });
    sw.Stop();
    return res;
}

template<typename T, int rescale_updater>
void PheKit::opInplace(Ciphertext *a,
                       const T &b,
                       const std::function<void(Ciphertext *, const T &)> &
                       op_f) {
    op_f(a, b);
    a->rescale(rescale_updater);
}

template<typename T, int rescale_updater>
void PheKit::opInplace(Ciphertext *a,
                       const T *b,
                       const size_t size,
                       const std::function<void(Ciphertext *, const T &)> &
                       op_f,
                       const std::string &mark) {
    sw.Mark(mark);
    ParallelFor(size, [&](const int i) {
        opInplace<T, rescale_updater>(&a[i], b[i], op_f);
    });
    sw.Stop();
}

template<int rescale_updater>
Ciphertext *PheKit::op_(const Ciphertext *a,
                        const double *b,
                        const size_t size,
                        const std::function<void(Ciphertext *, const Plaintext &)> &op_f,
                        const std::string &mark) {
    auto *pts = new Plaintext[size];
    ParallelFor(size, [&](const int i) {
        pts[i] = encoder_f(b[i]);
    });
    auto res = op<Plaintext, rescale_updater>(a, pts, size, op_f, mark);
    delete[] pts;
    return res;
}

template<int rescale_updater>
void PheKit::opInplace_(Ciphertext *a,
                        const double *b,
                        const size_t size,
                        const std::function<void(Ciphertext *, const Plaintext &)> &op_f,
                        const std::string &mark) {
    auto *pts = new Plaintext[size];
    ParallelFor(size, [&](const int i) {
        pts[i] = encoder_f(b[i]);
    });
    opInplace<Plaintext, rescale_updater>(a, pts, size, op_f, mark);
    delete[] pts;
}

template<typename T>
T *PheKit::histogram(const T *grad_pairs, int **indexes, const int *index_size, const int num_bins,
                     const int num_features, std::function<void(T &, const T &)> op_fun,
                     std::function<void(T &)> init_fun, const std::string &mark) {
    sw.Mark(mark);
    const auto total_bins = num_bins * num_features;
    auto *res = new T[total_bins];

    ParallelFor(num_features, [&](const size_t fidx) {
        for (int i = 0; i < num_bins; ++i) {
            const auto k = fidx * num_bins + i;
            init_fun(res[k]);
            for (int j = 0; j < index_size[k]; ++j) {
                op_fun(res[k], grad_pairs[indexes[k][j]]);
            }
        }
    });
    /*for (size_t i = 0; i < total_bins; ++i) {
        init_fun(res[i]);
        for (int j = 0; j < index_size[i]; ++j) {
            op_fun(res[i], grad_pairs[indexes[i][j]]);
        }
    }*/
    sw.Stop();

    return res;
}

//**************************************************PheKit Protect End**************************************************

//**************************************************PheKit Public Begin*************************************************
PheKit::PheKit(const SchemaType schema, size_t key_size, const int64_t scale, const int scale_cnt,
               const std::string &curve_name, const bool register_ec_lib): scale_cnt_(scale_cnt) {
    if (register_ec_lib) {
        yacl::crypto::register_ec_lib();
    }

    sw.Mark("key_pair_gen");
    if (std::vector curve_schemas = {SchemaType::ElGamal};
        std::find(curve_schemas.begin(), curve_schemas.end(), schema) == curve_schemas.end() || curve_name.empty()) {
        he_kit_ = std::make_shared<heu::lib::phe::HeKit>(schema, key_size);
    } else {
        std::cout << "schema: " << schema << ", curve_name: " << curve_name << std::endl;
        heu::lib::algorithms::elgamal::SecretKey sk;
        heu::lib::algorithms::elgamal::PublicKey pk;
        heu::lib::algorithms::elgamal::KeyGenerator::Generate(curve_name, &sk, &pk);
        he_kit_ = std::make_shared<heu::lib::phe::HeKit>(std::make_shared<heu::lib::phe::PublicKey>(std::move(pk)),
                                                         std::make_shared<heu::lib::phe::SecretKey>(std::move(sk)));
    }
    sw.Stop();

    decryptor_ = he_kit_->GetDecryptor();
    init(he_kit_, scale);
}

PheKit::PheKit(const SchemaType schema, const std::string &curve_name, const bool register_ec_lib): PheKit(
    schema, 2048, 1e6, 10, curve_name, register_ec_lib) {
}

PheKit::PheKit(const std::string &pk_buffer, const std::string &sk_buffer, const int64_t scale, const int scale_cnt,
               const bool register_ec_lib) : PheKit(
    yacl::ByteContainerView(pk_buffer.data(), pk_buffer.size()), getBuffer(sk_buffer),
    scale, scale_cnt, register_ec_lib) {
}

PheKit::PheKit(const std::string &pk_buffer, const int64_t scale, const int scale_cnt,
               const bool register_ec_lib) : PheKit(pk_buffer, "", scale, scale_cnt, register_ec_lib) {
}

std::string PheKit::pubKey() const {
    return std::string(getPublicKey()->Serialize());
}

std::string PheKit::secretKey() const {
    return has_secret_key ? std::string(getSecretKey()->Serialize()) : "";
}

Ciphertext *PheKit::encrypt(const double m) {
    return encrypt(encoder_f, m);
}

Ciphertext *PheKit::encrypts(const double *ms, const size_t size, const std::string &mark) {
    return encrypts(size, [&](const int i) {
        return encoder_f(ms[i]);
    }, mark);
}

Ciphertext *PheKit::encryptPair(const double m1, const double m2, const bool unpack) {
    return unpack ? encryptPairUnpack(m1, m2) : encrypt(batch_encoder_f, m1, m2);
}

Ciphertext *PheKit::encryptPairs(const double *ms1, const double *ms2, const size_t size, const bool unpack,
                                 const std::string &mark) {
    return unpack
               ? encryptPairsUnpack(ms1, ms2, size, mark)
               : encrypts(size, [&](const int i) {
                   return batch_encoder_f(ms1[i], ms2[i]);
               }, mark);
}

double PheKit::decrypt(const Ciphertext &ct) {
    double out;
    decrypt<double>(ct, &out, decoder_f);
    return out;
}

void PheKit::decrypts(const Ciphertext *cts, const size_t size, double *out, const std::string &mark) {
    decrypts<double>(cts, size, out, decoder_f, mark);
}

double *PheKit::decrypts(const Ciphertext *cts, const size_t size, const std::string &mark) {
    auto *out = new double[size];
    decrypts(cts, size, out, mark);
    return out;
}

void PheKit::decryptPair(const Ciphertext &ct, double *out, const bool unpack) {
    unpack ? decrypts(&ct, 2, out, "") : decrypt<double>(ct, out, batch_decoder_f);
    //std::cout << "[c++]out: [" << out[0] << ", " << out[1] << "]" << std::endl;
}

double *PheKit::decryptPair_(const Ciphertext &ct, const bool unpack) {
    auto *out = new double[2];
    decryptPair(ct, out, unpack);
    return out;
}

void PheKit::decryptPairs(const Ciphertext *cts, const size_t size, double *out, const bool unpack,
                          const std::string &mark) {
    unpack
        ? decrypts(cts, size * 2, out, mark)
        : decrypts<double>(cts, size, out, [&](const Plaintext &pt, double *o, const int scale_cnt) {
            o[0] = batch_encoders_.at(scale_cnt)->Decode<double, 0>(pt);
            o[size] = batch_encoders_.at(scale_cnt)->Decode<double, 1>(pt);
        }, mark);
}

double *PheKit::decryptPairs(const Ciphertext *cts, const size_t size, const bool unpack, const std::string &mark) {
    auto *out = new double[size * 2];
    decryptPairs(cts, size, out, unpack, mark);
    return out;
}

Ciphertext *PheKit::add(const Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    return unpack ? adds(&ct1, &ct2, 2, "") : op(ct1, ct2, add_f);
}

Ciphertext *PheKit::add(const Ciphertext &ct1, const double pt2, const bool unpack) {
    return unpack ? adds(&ct1, &pt2, 2, "") : op<Plaintext>(ct1, encoder_f(pt2), add_p_f);
}

Ciphertext *PheKit::adds(const Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    return op(cts1, cts2, size, add_f, mark);
}

Ciphertext *PheKit::adds(const Ciphertext *cts1, const double *pts2, const size_t size, const std::string &mark) {
    return op_(cts1, pts2, size, add_p_f, mark);
}

void PheKit::addInplace(Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    unpack ? addInplaces(&ct1, &ct2, 2, "") : opInplace(&ct1, ct2, add_f);
}

void PheKit::addInplace(Ciphertext &ct1, const double pt2, const bool unpack) {
    unpack ? addInplaces(&ct1, &pt2, 2, "") : opInplace<Plaintext>(&ct1, encoder_f(pt2), add_p_f);
}

void PheKit::addInplaces(Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    opInplace(cts1, cts2, size, add_f, mark);
}

void PheKit::addInplaces(Ciphertext *cts1, const double *pts2, const size_t size, const std::string &mark) {
    opInplace_(cts1, pts2, size, add_p_f, mark);
}

Ciphertext *PheKit::sub(const Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    return unpack ? subs(&ct1, &ct2, 2, "") : op(ct1, ct2, sub_f);
}

Ciphertext *PheKit::sub(const Ciphertext &ct1, const double pt2, const bool unpack) {
    return unpack ? subs(&ct1, &pt2, 2, "") : op<Plaintext>(ct1, encoder_f(pt2), sub_p_f);
}

Ciphertext *PheKit::subs(const Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    return op(cts1, cts2, size, sub_f, mark);
}

Ciphertext *PheKit::subs(const Ciphertext *cts1, const double *pts2, const size_t size, const std::string &mark) {
    return op_(cts1, pts2, size, sub_p_f, mark);
}

void PheKit::subInplace(Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    unpack ? subInplaces(&ct1, &ct2, 2, "") : opInplace(&ct1, ct2, sub_f);
}

void PheKit::subInplace(Ciphertext &ct1, const double pt2, const bool unpack) {
    unpack ? subInplaces(&ct1, &pt2, 2, "") : opInplace<Plaintext>(&ct1, encoder_f(pt2), sub_p_f);
}

void PheKit::subInplaces(Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    opInplace(cts1, cts2, size, sub_f, mark);
}

void PheKit::subInplaces(Ciphertext *cts1, const double *pts2, const size_t size, const std::string &mark) {
    opInplace_(cts1, pts2, size, sub_p_f, mark);
}

[[nodiscard]] Ciphertext *PheKit::mul(const Ciphertext &ct1, const double pt2, const bool unpack) {
    return unpack ? muls(&ct1, &pt2, 2, "") : op<Plaintext, 1>(ct1, encoder_f(pt2), mul_p_f);
}

Ciphertext *PheKit::muls(const Ciphertext *cts1, const double *pts2, const size_t size, const std::string &mark) {
    return op_<1>(cts1, pts2, size, mul_p_f, mark);
}

void PheKit::mulInplace(Ciphertext &ct1, const double pt2, const bool unpack) {
    unpack ? mulInplaces(&ct1, &pt2, 2, "") : opInplace<Plaintext, 1>(&ct1, encoder_f(pt2), mul_p_f);
}

void PheKit::mulInplaces(Ciphertext *cts1, const double *pts2, const size_t size, const std::string &mark) {
    opInplace_<1>(cts1, pts2, size, mul_p_f, mark);
}

Ciphertext *PheKit::negate(const Ciphertext &ct) const {
    const auto res = new Ciphertext(evaluator_->Negate(ct.c_data()));
    res->set_scale_cnt(ct.scale_cnt());
    return res;
}

Ciphertext *PheKit::negates(const Ciphertext *cts, const size_t size, const std::string &mark) {
    sw.Mark(mark);
    auto *res = new Ciphertext[size];
    ParallelFor(size, [&](const int i) {
        res[i] = evaluator_->Negate(cts[i].c_data());
        res[i].set_scale_cnt(cts[i].scale_cnt());
    });
    sw.Stop();
    return res;
}

void PheKit::negateInplace(const Ciphertext &ct) const {
    evaluator_->NegateInplace(ct.data());
}

void PheKit::negateInplaces(const Ciphertext *cts, const size_t size, const std::string &mark) {
    sw.Mark(mark);
    ParallelFor(size, [&](const int i) {
        evaluator_->NegateInplace(cts[i].data());
    });
    sw.Stop();
}

Ciphertext *PheKit::histogram(const Ciphertext *grad_pairs, int **indexes, const int *index_size,
                              const int num_bins, int const num_features, const std::string &mark) {
    return histogram<Ciphertext>(grad_pairs, indexes, index_size, num_bins, num_features,
                                 [&](Ciphertext &res, const Ciphertext &grad_pair) {
                                     addInplace(res, grad_pair);
                                 }, [&](Ciphertext &res) {
                                     res.copy_from(zero);
                                 }, mark);
}

double *PheKit::histogram(const double *grad_pairs, int **indexes, const int *index_size,
                          const int num_bins, const int num_features, const std::string &mark) {
    return histogram<double>(grad_pairs, indexes, index_size, num_bins, num_features,
                             [&](double &res, const double &grad_pair) {
                                 res += grad_pair;
                             }, [&](double &res) {
                                 res = 0;
                             }, mark);
}

void PheKit::prettyPrint(const uint8_t time_unit) const {
    sw.PrettyPrint(static_cast<TimeUnit>(time_unit));
}

//**************************************************PheKit Public End***************************************************

//*****************************************************Global Begin*****************************************************
void deletePheKit(const PheKit *pheKit) {
    delete pheKit;
}

void deleteCiphertext(const Ciphertext *ciphertext) {
    delete ciphertext;
}

void deleteCiphertexts(const Ciphertext *ciphertext) {
    delete[] ciphertext;
}

std::string cipher2Bytes(const Ciphertext &ciphertext) {
    return std::string(ciphertext.c_data().Serialize());
}

Ciphertext *bytes2Cipher(const std::string &buffer) {
    auto *out = new Ciphertext();
    out->deserialize(yacl::ByteContainerView(buffer.data(), buffer.size()));
    return out;
}

HeBuffer *ciphers2Bytes(const Ciphertext *ciphertexts, const size_t size) {
    auto *out = new HeBuffer(size);
    ParallelFor(size, [&](const auto i) {
        out->set(i, std::string(ciphertexts[i].c_data().Serialize()));
    });
    return out;
}

Ciphertext *bytes2Ciphers(const HeBuffer &buffers, const size_t size) {
    auto *out = new Ciphertext[size];
    ParallelFor(size, [&](const auto i) {
        out[i].deserialize(yacl::ByteContainerView(buffers[i].data(), buffers[i].size()));
    });
    return out;
}

std::pair<int **, const int *> genIndexes(const int n, const int num_features, const int num_bins) {
    std::mt19937 rng(std::random_device{}());
    std::uniform_int_distribution dist(0, num_bins - 1);

    const auto total_bins = num_features * num_bins;
    auto indexes = new int *[total_bins];
    auto index_size = new int[total_bins]{};

    for (int j = 0; j < num_features; ++j) {
        int flag[n];
        for (int i = 0; i < n; ++i) {
            flag[i] = dist(rng);
            index_size[j * num_bins + flag[i]]++;
        }

        for (int i = 0; i < num_bins; ++i) {
            const auto k = j * num_bins + i;
            indexes[k] = new int[index_size[k]];
            index_size[k] = 0;
        }

        for (int i = 0; i < n; ++i) {
            const int k = j * num_bins + flag[i];
            indexes[k][index_size[k]] = i;
            index_size[k]++;
        }
    }

    return std::pair{indexes, index_size};
}

//*****************************************************Global End*******************************************************
