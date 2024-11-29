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

Ciphertext *PheKit::encrypt(double data) {
  return encrypt(encoder_f, data);
}

Ciphertext *PheKit::encrypt(double *data, size_t size) {
  return encrypt(size, [&](int i) {
    return encoder_f(data[i]);
  });
}

double PheKit::decrypt(const Ciphertext &ct) {
  return decrypt<double>(ct, decoder_f);
}

double *PheKit::decrypt(const Ciphertext *ct, size_t size) {
  return decrypt<double>(ct, size, decoder_f);
}

Ciphertext *PheKit::encryptPair(double d1, double d2) {
  return encrypt(batch_encoder_f, d1, d2);
}

Ciphertext *PheKit::encryptPair(double *d1, double *d2, size_t size) {
  return encrypt(size, [&](int i) {
    return batch_encoder_f(d1[i], d2[i]);
  });
}

double *PheKit::decryptPair(const Ciphertext &ct) {
  return decrypt<double *>(ct, [&](const Plaintext &pt) {
    double *res = new double[2];
    res[0] = batch_encoder_->Decode<double, 0>(pt);
    res[1] = batch_encoder_->Decode<double, 1>(pt);
    return res;
  });
}

double **PheKit::decryptPair(const Ciphertext *ct, size_t size) {
  return decrypt<double *>(ct, size, [&](const Plaintext &pt) {
    double *res = new double[2];
    res[0] = batch_encoder_->Decode<double, 0>(pt);
    res[1] = batch_encoder_->Decode<double, 1>(pt);
    return res;
  });
}

Ciphertext *PheKit::add(const Ciphertext &ct1, const Ciphertext &ct2) {
  auto res = new Ciphertext();
  *res = ct1;
  evaluator_->AddInplace(res, ct2);
  return res;
}

void PheKit::addInplace(Ciphertext &ct1, const Ciphertext &ct2) {
  evaluator_->AddInplace(&ct1, ct2);
}
