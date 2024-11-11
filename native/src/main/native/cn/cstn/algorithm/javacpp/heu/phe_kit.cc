//
// Created by 2024 dterazhao.
//

#include "heu/phe_kit.h"

PheKit::PheKit(SchemaType schema_type, size_t key_size, int64_t scale) {
  he_kit_ = std::make_shared<heu::lib::phe::HeKit>(schema_type, key_size);
  encryptor_ = he_kit_->GetEncryptor();
  decryptor_ = he_kit_->GetDecryptor();
  evaluator_ = he_kit_->GetEvaluator();
  encoder_ = std::make_shared<heu::lib::phe::PlainEncoder>(
      he_kit_->GetEncoder<heu::lib::phe::PlainEncoder>(scale));
  batch_encoder_ = std::make_shared<heu::lib::phe::BatchEncoder>(
      he_kit_->GetEncoder<heu::lib::phe::BatchEncoder>(scale));
}

PheKit::PheKit(uint8_t schema_type, size_t key_size, int64_t scale)
    : PheKit((SchemaType)schema_type, key_size, scale) {}

PheKit::PheKit(yacl::ByteContainerView pk_buffer) {
  dhe_kit_ = std::make_shared<heu::lib::phe::DestinationHeKit>(pk_buffer);
  encryptor_ = dhe_kit_->GetEncryptor();
  evaluator_ = dhe_kit_->GetEvaluator();
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
  auto pt = encoder_->Encode(data);
  auto res = new Ciphertext();
  *res = encryptor_->Encrypt(pt);
  return res;
}

double PheKit::decrypt(const Ciphertext &ct) {
  heu::lib::phe::Plaintext out;
  decryptor_->Decrypt(ct, &out);
  return encoder_->Decode<double>(out);
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
