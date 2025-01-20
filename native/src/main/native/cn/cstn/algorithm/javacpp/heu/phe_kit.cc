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


//**************************************************PheKit Begin**************************************************
PheKit::PheKit(const SchemaType schema, size_t key_size, const int64_t scale, const std::string &curve_name,
               const bool register_ec_lib) {
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
    decryptor_ = he_kit_->GetDecryptor();
    encryptor_ = he_kit_->GetEncryptor();
    evaluator_ = he_kit_->GetEvaluator();

    init(he_kit_, scale);
    sw.Stop();
}

PheKit::PheKit(const SchemaType schema, const std::string &curve_name, const bool register_ec_lib): PheKit(
    schema, 2048, 1e6, curve_name, register_ec_lib) {
}

PheKit::PheKit(yacl::ByteContainerView pk_buffer, const int64_t scale, const bool register_ec_lib) : has_secret_key(
    false) {
    if (register_ec_lib) {
        yacl::crypto::register_ec_lib();
    }
    dhe_kit_ = std::make_shared<heu::lib::phe::DestinationHeKit>(pk_buffer);
    encryptor_ = dhe_kit_->GetEncryptor();
    evaluator_ = dhe_kit_->GetEvaluator();
    init(dhe_kit_, scale);
}

PheKit::PheKit(const std::string &pk_buffer, const int64_t scale, const bool register_ec_lib) : PheKit(
    yacl::ByteContainerView(pk_buffer.data(), pk_buffer.size()), scale, register_ec_lib) {
}

void PheKit::init(const std::shared_ptr<heu::lib::phe::HeKitPublicBase> &he_kit, int64_t scale) {
    if (he_kit->GetSchemaType() == SchemaType::ElGamal) {
        scale = 1e4;
    }
    encoder_ = std::make_shared<heu::lib::phe::PlainEncoder>(
        he_kit->GetEncoder<heu::lib::phe::PlainEncoder>(scale));
    batch_encoder_ = std::make_shared<heu::lib::phe::BatchEncoder>(
        he_kit->GetEncoder<heu::lib::phe::BatchEncoder>(scale));

    encoder_f = [this](auto &&m) { return encoder_->Encode(std::forward<decltype(m)>(m)); };
    decoder_f = [&](const Plaintext &pt, double *out) {
        *out = encoder_->Decode<double>(pt);
    };
    batch_encoder_f = [this](auto &&m1, auto &&m2) {
        return batch_encoder_->Encode(std::forward<decltype(m1)>(m1), std::forward<decltype(m2)>(m2));
    };
    batch_decoder_f = [&](const Plaintext &pt, double *out) {
        out[0] = batch_encoder_->Decode<double, 0>(pt);
        out[1] = batch_encoder_->Decode<double, 1>(pt);
    };
    add_f = [&](Ciphertext *res, const Ciphertext &b) { evaluator_->AddInplace(res, b); };
    sub_f = [&](Ciphertext *res, const Ciphertext &b) { evaluator_->SubInplace(res, b); };
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

inline Ciphertext *PheKit::op(const Ciphertext &a,
                              const Ciphertext &b,
                              const std::function<void(Ciphertext *, const Ciphertext &)> &op_f) {
    const auto res = new Ciphertext();
    *res = a;
    op_f(res, b);
    return res;
}

inline Ciphertext *PheKit::op(const Ciphertext *a,
                              const Ciphertext *b,
                              const size_t size,
                              const std::function<void(Ciphertext *, const Ciphertext &)> &op_f,
                              const std::string &mark) {
    sw.Mark(mark);
    auto *res = new Ciphertext[size];
    ParallelFor(size, [&](const int i) {
        res[i] = a[i];
        opInplace(&res[i], b[i], op_f);
    });
    sw.Stop();
    return res;
}

inline void PheKit::opInplace(Ciphertext *a,
                              const Ciphertext &b,
                              const std::function<void(Ciphertext *, const Ciphertext &)> &op_f) {
    op_f(a, b);
}

inline void PheKit::opInplace(Ciphertext *a,
                              const Ciphertext *b,
                              const size_t size,
                              const std::function<void(Ciphertext *, const Ciphertext &)> &op_f,
                              const std::string &mark) {
    sw.Mark(mark);
    ParallelFor(size, [&](const int i) {
        opInplace(&a[i], b[i], op_f);
    });
    sw.Stop();
}

std::string PheKit::pubKey() const {
    return std::string(getPublicKey()->Serialize());
}

Ciphertext *PheKit::encrypt(const double m) {
    return encrypt(encoder_f, m);
}

Ciphertext *PheKit::encrypts(const double *ms, const size_t size, const std::string &mark) {
    return encrypts(size, [&](const int i) {
        return encoder_f(ms[i]);
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
        : decrypts<double>(cts, size, out, [&](const Plaintext &pt, double *o) {
            o[0] = batch_encoder_->Decode<double, 0>(pt);
            o[size] = batch_encoder_->Decode<double, 1>(pt);
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

Ciphertext *PheKit::adds(const Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    return op(cts1, cts2, size, add_f, mark);
}

void PheKit::addInplace(Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    unpack ? addInplaces(&ct1, &ct2, 2, "") : opInplace(&ct1, ct2, add_f);
}

void PheKit::addInplaces(Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    opInplace(cts1, cts2, size, add_f, mark);
}

Ciphertext *PheKit::sub(const Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    return unpack ? subs(&ct1, &ct2, 2, "") : op(ct1, ct2, sub_f);
}

Ciphertext *PheKit::subs(const Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    return op(cts1, cts2, size, sub_f, mark);
}

void PheKit::subInplace(Ciphertext &ct1, const Ciphertext &ct2, const bool unpack) {
    unpack ? subInplaces(&ct1, &ct2, 2, "") : opInplace(&ct1, ct2, sub_f);
}

void PheKit::subInplaces(Ciphertext *cts1, const Ciphertext *cts2, const size_t size, const std::string &mark) {
    opInplace(cts1, cts2, size, sub_f, mark);
}

void PheKit::prettyPrint(const uint8_t time_unit) const {
    sw.PrettyPrint(static_cast<TimeUnit>(time_unit));
}

//**************************************************PheKit End****************************************************

//**************************************************Global Begin**************************************************
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
    return std::string(ciphertext.Serialize());
}

Ciphertext *bytes2Cipher(const std::string &buffer) {
    auto *out = new Ciphertext();
    out->Deserialize(yacl::ByteContainerView(buffer.data(), buffer.size()));
    return out;
}

HeBuffer *ciphers2Bytes(const Ciphertext *ciphertexts, const size_t size) {
    auto *out = new HeBuffer(size);
    ParallelFor(size, [&](const auto i) {
        out->set(i, std::string(ciphertexts[i].Serialize()));
    });
    return out;
}

Ciphertext *bytes2Ciphers(const HeBuffer &buffers, const size_t size) {
    auto *out = new Ciphertext[size];
    ParallelFor(size, [&](const auto i) {
        out[i].Deserialize(yacl::ByteContainerView(buffers[i].data(), buffers[i].size()));
    });
    return out;
}

//**************************************************Global End****************************************************
