//
// Created by 2024 dterazhao.
//

#include "heu/phe_kit.h"

PheKit::PheKit(SchemaType schema_type, size_t key_size, int64_t scale) {
  sw.Mark("key_pair_gen");
  he_kit_ = std::make_shared<heu::lib::phe::HeKit>(schema_type, key_size);
  decryptor_ = he_kit_->GetDecryptor();
  encryptor_ = he_kit_->GetEncryptor();
  evaluator_ = he_kit_->GetEvaluator();
  init(he_kit_, scale);
  sw.PrintWithMills("key_pair_gen");
}

PheKit::PheKit(uint8_t schema_type, size_t key_size, int64_t scale)
    : PheKit((SchemaType) schema_type, key_size, scale) {}

PheKit::PheKit(yacl::ByteContainerView pk_buffer, int64_t scale) : has_secret_key(false) {
  dhe_kit_ = std::make_shared<heu::lib::phe::DestinationHeKit>(pk_buffer);
  encryptor_ = dhe_kit_->GetEncryptor();
  evaluator_ = dhe_kit_->GetEvaluator();
  init(dhe_kit_, scale);
}

void PheKit::init(std::shared_ptr<heu::lib::phe::HeKitPublicBase> he_kit, int64_t scale) {
  encoder_ = std::make_shared<heu::lib::phe::PlainEncoder>(
      he_kit->GetEncoder<heu::lib::phe::PlainEncoder>(scale));
  batch_encoder_ = std::make_shared<heu::lib::phe::BatchEncoder>(
      he_kit->GetEncoder<heu::lib::phe::BatchEncoder>(scale));
  encoder_f = std::bind(&heu::lib::phe::PlainEncoder::Encode<double>, encoder_, std::placeholders::_1);
  decoder_f = std::bind(&heu::lib::phe::PlainEncoder::Decode<double>, encoder_, std::placeholders::_1);
  batch_encoder_f = std::bind(&heu::lib::phe::BatchEncoder::Encode<double>, batch_encoder_,
                              std::placeholders::_1, std::placeholders::_2);
  batch_decoder_f = [&](const Plaintext &pt) {
    double *res = new double[2];
    res[0] = batch_encoder_->Decode<double, 0>(pt);
    res[1] = batch_encoder_->Decode<double, 1>(pt);
    return res;
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

Ciphertext *PheKit::encrypt(double m) {
  return encrypt(encoder_f, m);
}

Ciphertext *PheKit::encrypt(double *ms, size_t size) {
  return encrypt(size, [&](int i) {
    return encoder_f(ms[i]);
  });
}

double PheKit::decrypt(const Ciphertext &ct) {
  return decrypt<double>(ct, decoder_f);
}

double *PheKit::decrypt(const Ciphertext *cts, size_t size) {
  return decrypt<double>(cts, size, decoder_f);
}

Ciphertext *PheKit::encryptPair(double m1, double m2) {
  return encrypt(batch_encoder_f, m1, m2);
}

Ciphertext *PheKit::encryptPair(double *ms1, double *ms2, size_t size) {
  return encrypt(size, [&](int i) {
    return batch_encoder_f(ms1[i], ms2[i]);
  });
}

double *PheKit::decryptPair(const Ciphertext &ct) {
  return decrypt<double *>(ct, batch_decoder_f);
}

double **PheKit::decryptPair(const Ciphertext *cts, size_t size) {
  return decrypt<double *>(cts, size, batch_decoder_f);
}

inline Ciphertext *PheKit::op(const Ciphertext &a,
                              const Ciphertext &b,
                              std::function<void(Ciphertext *, const Ciphertext &)> op_f) {
  auto res = new Ciphertext();
  *res = a;
  op_f(res, b);
  return res;
}

inline Ciphertext *PheKit::op(const Ciphertext *a,
                              const Ciphertext *b,
                              size_t size,
                              std::function<void(Ciphertext *, const Ciphertext &)> op_f, const std::string &op_name) {
  sw.Mark(op_name);
  Ciphertext *res = new Ciphertext[size];
  /*ParallelFor(size, [&](int i) {
    res[i] = a[i];
    opInplace(&res[i], b[i], op_f);
  });*/
  for (size_t i = 0; i < size; ++i) {
    res[i] = a[i];
    opInplace(&res[i], b[i], op_f);
  }
  sw.PrintWithMills(op_name);
  return res;
}

inline void PheKit::opInplace(Ciphertext *a,
                              const Ciphertext &b,
                              std::function<void(Ciphertext *, const Ciphertext &)> op_f) {
  op_f(a, b);
}

inline void PheKit::opInplace(Ciphertext *a,
                              const Ciphertext *b,
                              size_t size,
                              std::function<void(Ciphertext *, const Ciphertext &)> op_f, const std::string &op_name) {
  sw.Mark(op_name);
  /*ParallelFor(size, [&](int i) {
    opInplace(&a[i], b[i], op_f);
  });*/
  for (size_t i = 0; i < size; ++i) {
    opInplace(&a[i], b[i], op_f);
  }
  sw.PrintWithMills(op_name);
}

Ciphertext *PheKit::add(const Ciphertext &ct1, const Ciphertext &ct2) {
  return op(ct1, ct2, add_f);
}

Ciphertext *PheKit::add(const Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  return op(cts1, cts2, size, add_f, "add");
}

void PheKit::addInplace(Ciphertext &ct1, const Ciphertext &ct2) {
  opInplace(&ct1, ct2, add_f);
}

void PheKit::addInplace(Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  opInplace(cts1, cts2, size, add_f, "addInplace");
}

Ciphertext *PheKit::sub(const Ciphertext &ct1, const Ciphertext &ct2) {
  return op(ct1, ct2, sub_f);
}

Ciphertext *PheKit::sub(const Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  return op(cts1, cts2, size, sub_f, "sub");
}

void PheKit::subInplace(Ciphertext &ct1, const Ciphertext &ct2) {
  opInplace(&ct1, ct2, sub_f);
}

void PheKit::subInplace(Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  opInplace(cts1, cts2, size, sub_f, "subInplace");
}
