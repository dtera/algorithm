//
// Created by 2024 dterazhao.
//

#include "heu/phe_kit.h"

const std::string PheKit::empty;
const std::string PheKit::ed25519 = "ed25519";
const std::string PheKit::curve25519 = "curve25519";
const std::string PheKit::sm2 = "sm2";
const std::string PheKit::secp256k1 = "secp256k1";
const std::string PheKit::secp192r1 = "secp192r1";
const std::string PheKit::secp256r1 = "secp256r1";
const std::string PheKit::fourq = "fourq";

PheKit::PheKit(const SchemaType schema, size_t key_size, const int64_t scale, const std::string &curve_name) {
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
    sw.PrintWithMills("key_pair_gen");
}

PheKit::PheKit(const SchemaType schema, const std::string &curve_name): PheKit(schema, 2048, 1e6, curve_name) {
}

PheKit::PheKit(yacl::ByteContainerView pk_buffer, const int64_t scale) : has_secret_key(false) {
    dhe_kit_ = std::make_shared<heu::lib::phe::DestinationHeKit>(pk_buffer);
    encryptor_ = dhe_kit_->GetEncryptor();
    evaluator_ = dhe_kit_->GetEvaluator();
    init(dhe_kit_, scale);
}

PheKit::PheKit(const std::string &pk_buffer, const int64_t scale) : PheKit(
    yacl::ByteContainerView(pk_buffer.data(), pk_buffer.size()), scale) {
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
                              const std::string &op_name) {
    sw.Mark(op_name);
    auto *res = new Ciphertext[size];
    ParallelFor(size, [&](const int i) {
        res[i] = a[i];
        opInplace(&res[i], b[i], op_f);
    });
    sw.PrintWithMills(op_name);
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
                              const std::string &op_name) {
    sw.Mark(op_name);
    ParallelFor(size, [&](const int i) {
        opInplace(&a[i], b[i], op_f);
    });
    sw.PrintWithMills(op_name);
}

std::string PheKit::pubKey() const {
    return std::string(getPublicKey()->Serialize());
}

Ciphertext *PheKit::encrypt(const double m) {
    return encrypt(encoder_f, m);
}

Ciphertext *PheKit::encrypts(const double *ms, const size_t size) {
    return encrypts(size, [&](const int i) {
        return encoder_f(ms[i]);
    });
}

double PheKit::decrypt(const Ciphertext &ct) {
    double out;
    decrypt<double>(ct, &out, decoder_f);
    return out;
}

void PheKit::decrypts(const Ciphertext *cts, const size_t size, double *out) {
    decrypts<double>(cts, size, out, decoder_f);
}

double *PheKit::decrypts(const Ciphertext *cts, const size_t size) {
    auto *out = new double[size];
    decrypts(cts, size, out);
    return out;
}

Ciphertext *PheKit::encryptPair(const double m1, const double m2) {
    return encrypt(batch_encoder_f, m1, m2);
}

Ciphertext *PheKit::encryptPairs(const double *ms1, const double *ms2, const size_t size) {
    return encrypts(size, [&](const int i) {
        return batch_encoder_f(ms1[i], ms2[i]);
    });
}

void PheKit::decryptPair(const Ciphertext &ct, double *out) {
    decrypt<double>(ct, out, batch_decoder_f);
    //std::cout << "[c++]out: [" << out[0] << ", " << out[1] << "]" << std::endl;
}

double *PheKit::decryptPair_(const Ciphertext &ct) {
    auto *out = new double[2];
    decryptPair(ct, out);
    return out;
}

void PheKit::decryptPairs(const Ciphertext *cts, const size_t size, double *out) {
    decrypts<double>(cts, size, out, [&](const Plaintext &pt, double *o) {
        o[0] = batch_encoder_->Decode<double, 0>(pt);
        o[size] = batch_encoder_->Decode<double, 1>(pt);
    });
}

double *PheKit::decryptPairs(const Ciphertext *cts, const size_t size) {
    auto *out = new double[size * 2];
    decryptPairs(cts, size, out);
    return out;
}

Ciphertext *PheKit::add(const Ciphertext &ct1, const Ciphertext &ct2) const {
    return op(ct1, ct2, add_f);
}

Ciphertext *PheKit::adds(const Ciphertext *cts1, const Ciphertext *cts2, const size_t size) {
    return op(cts1, cts2, size, add_f, "adds");
}

void PheKit::addInplace(Ciphertext &ct1, const Ciphertext &ct2) const {
    opInplace(&ct1, ct2, add_f);
}

void PheKit::addInplaces(Ciphertext *cts1, const Ciphertext *cts2, const size_t size) {
    opInplace(cts1, cts2, size, add_f, "addInplaces");
}

Ciphertext *PheKit::sub(const Ciphertext &ct1, const Ciphertext &ct2) const {
    return op(ct1, ct2, sub_f);
}

Ciphertext *PheKit::subs(const Ciphertext *cts1, const Ciphertext *cts2, const size_t size) {
    return op(cts1, cts2, size, sub_f, "subs");
}

void PheKit::subInplace(Ciphertext &ct1, const Ciphertext &ct2) const {
    opInplace(&ct1, ct2, sub_f);
}

void PheKit::subInplaces(Ciphertext *cts1, const Ciphertext *cts2, const size_t size) {
    opInplace(cts1, cts2, size, sub_f, "subInplaces");
}

void deletePheKit(const PheKit *pheKit) {
    delete pheKit;
}

void deleteCiphertext(const Ciphertext *ciphertext) {
    delete ciphertext;
}

void deleteCiphertexts(const Ciphertext *ciphertext) {
    delete[] ciphertext;
}
