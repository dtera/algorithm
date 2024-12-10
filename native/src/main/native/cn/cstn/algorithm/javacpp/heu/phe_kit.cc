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

PheKit::PheKit(yacl::ByteContainerView pk_buffer, int64_t scale) : has_secret_key(false) {
  dhe_kit_ = std::make_shared<heu::lib::phe::DestinationHeKit>(pk_buffer);
  encryptor_ = dhe_kit_->GetEncryptor();
  evaluator_ = dhe_kit_->GetEvaluator();
  init(dhe_kit_, scale);
}

PheKit::PheKit(const char *pk_buffer, int64_t scale) : PheKit(yacl::ByteContainerView(pk_buffer, scale)) {}

void PheKit::init(std::shared_ptr<heu::lib::phe::HeKitPublicBase> he_kit, int64_t scale) {
  encoder_ = std::make_shared<heu::lib::phe::PlainEncoder>(
      he_kit->GetEncoder<heu::lib::phe::PlainEncoder>(scale));
  batch_encoder_ = std::make_shared<heu::lib::phe::BatchEncoder>(
      he_kit->GetEncoder<heu::lib::phe::BatchEncoder>(scale));

  encoder_f = std::bind(&heu::lib::phe::PlainEncoder::Encode<double>, encoder_, std::placeholders::_1);
  decoder_f = [&](const Plaintext &pt, double *out) {
    *out = encoder_->Decode<double>(pt);
  };
  batch_encoder_f = std::bind(&heu::lib::phe::BatchEncoder::Encode<double>, batch_encoder_,
                              std::placeholders::_1, std::placeholders::_2);
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

Ciphertext *PheKit::encrypt(double m) {
  return encrypt(encoder_f, m);
}

Ciphertext *PheKit::encrypts(double *ms, size_t size) {
  return encrypts(size, [&](int i) {
    return encoder_f(ms[i]);
  });
}

double PheKit::decrypt(const Ciphertext &ct) {
  double out;
  decrypt<double>(ct, &out, decoder_f);
  return out;
}

void PheKit::decrypts(const Ciphertext *cts, size_t size, double *out) {
  decrypts<double>(cts, size, out, decoder_f);
}

double *PheKit::decrypts(const Ciphertext *cts, size_t size) {
  double *out = new double[size];
  decrypts(cts, size, out);
  return out;
}

Ciphertext *PheKit::encryptPair(double m1, double m2) {
  return encrypt(batch_encoder_f, m1, m2);
}

Ciphertext *PheKit::encryptPairs(double *ms1, double *ms2, size_t size) {
  return encrypts(size, [&](int i) {
    return batch_encoder_f(ms1[i], ms2[i]);
  });
}

void PheKit::decryptPair(const Ciphertext &ct, double *out) {
  decrypt<double>(ct, out, batch_decoder_f);
  //std::cout << "[c++]out: [" << out[0] << ", " << out[1] << "]" << std::endl;
}

double *PheKit::decryptPair_(const Ciphertext &ct) {
  double *out = new double[2];
  decryptPair(ct, out);
  return out;
}

void PheKit::decryptPairs(const Ciphertext *cts, size_t size, double *out) {
  decrypts<double>(cts, size, out, [&](const Plaintext &pt, double *o) {
    o[0] = batch_encoder_->Decode<double, 0>(pt);
    o[size] = batch_encoder_->Decode<double, 1>(pt);
  });
}

double *PheKit::decryptPairs(const Ciphertext *cts, size_t size) {
  double *out = new double[size * 2];
  decryptPairs(cts, size, out);
  return out;
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

Ciphertext *PheKit::adds(const Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  return op(cts1, cts2, size, add_f, "adds");
}

void PheKit::addInplace(Ciphertext &ct1, const Ciphertext &ct2) {
  opInplace(&ct1, ct2, add_f);
}

void PheKit::addInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  opInplace(cts1, cts2, size, add_f, "addInplaces");
}

Ciphertext *PheKit::sub(const Ciphertext &ct1, const Ciphertext &ct2) {
  return op(ct1, ct2, sub_f);
}

Ciphertext *PheKit::subs(const Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  return op(cts1, cts2, size, sub_f, "subs");
}

void PheKit::subInplace(Ciphertext &ct1, const Ciphertext &ct2) {
  opInplace(&ct1, ct2, sub_f);
}

void PheKit::subInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size) {
  opInplace(cts1, cts2, size, sub_f, "subInplaces");
}
