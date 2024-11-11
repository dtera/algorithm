//
// Created by 2024 dterazhao.
//

#pragma once

#include "heu/library/phe/encoding/encoding.h"
#include "heu/library/phe/phe.h"

using SchemaType = heu::lib::phe::SchemaType;
using Ciphertext = heu::lib::phe::Ciphertext;

class PheKit {
 private:
  std::shared_ptr<heu::lib::phe::HeKit> he_kit_;
  std::shared_ptr<heu::lib::phe::DestinationHeKit> dhe_kit_;
  std::shared_ptr<heu::lib::phe::Encryptor> encryptor_;
  std::shared_ptr<heu::lib::phe::Decryptor> decryptor_;
  std::shared_ptr<heu::lib::phe::Evaluator> evaluator_;
  std::shared_ptr<heu::lib::phe::PlainEncoder> encoder_;
  std::shared_ptr<heu::lib::phe::BatchEncoder> batch_encoder_;
  bool has_secret_key;

 public:
  PheKit(SchemaType schema_type, size_t key_size = 2048, int64_t scale = 1e6);
  PheKit(uint8_t schema_type, size_t key_size = 2048, int64_t scale = 1e6);
  PheKit(yacl::ByteContainerView pk_buffer);

  bool hasSecretKey() const;
  const std::shared_ptr<heu::lib::phe::PublicKey> &getPublicKey() const;
  const std::shared_ptr<heu::lib::phe::SecretKey> &getSecretKey() const;

  Ciphertext *encrypt(double data);
  double decrypt(const Ciphertext &ct);
  Ciphertext *add(const Ciphertext &ct1, const Ciphertext &ct2);
  void addInplace(Ciphertext &ct1, const Ciphertext &ct2);
};
