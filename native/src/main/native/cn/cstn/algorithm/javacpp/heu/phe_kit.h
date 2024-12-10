//
// Created by 2024 dterazhao.
//

#pragma once

#include "heu/library/phe/phe.h"
#include "heu/library/phe/encoding/encoding.h"
#include "util/stopwatch.hpp"
#include "util/utils.h"

typedef heu::lib::phe::SchemaType SchemaType;
typedef heu::lib::phe::Ciphertext Ciphertext;
typedef heu::lib::phe::Plaintext Plaintext;

class PheKit {
 private:
  std::shared_ptr<heu::lib::phe::HeKit> he_kit_;
  std::shared_ptr<heu::lib::phe::DestinationHeKit> dhe_kit_;
  std::shared_ptr<heu::lib::phe::Encryptor> encryptor_;
  std::shared_ptr<heu::lib::phe::Decryptor> decryptor_;
  std::shared_ptr<heu::lib::phe::Evaluator> evaluator_;
  std::shared_ptr<heu::lib::phe::PlainEncoder> encoder_;
  std::shared_ptr<heu::lib::phe::BatchEncoder> batch_encoder_;
  bool has_secret_key = true;
  StopWatch sw;

 protected:
  std::function<Plaintext(double)> encoder_f;
  std::function<void(const Plaintext &, double *)> decoder_f;
  std::function<Plaintext(double, double)> batch_encoder_f;
  std::function<void(const Plaintext &, double *)> batch_decoder_f;
  std::function<void(Ciphertext *, const Ciphertext &)> add_f;
  std::function<void(Ciphertext *, const Ciphertext &)> sub_f;

  void init(std::shared_ptr<heu::lib::phe::HeKitPublicBase> he_kit, int64_t scale);

  template<typename ...ARGS>
  inline Ciphertext *encrypt(std::function<Plaintext(ARGS...)> encoder, ARGS ...args) {
    auto res = new Ciphertext();
    *res = encryptor_->Encrypt(encoder(std::forward<ARGS>(args)...));
    return res;
  }

  inline Ciphertext *encrypts(size_t size, std::function<Plaintext(int)> encoder) {
    sw.Mark("encrypts");
    auto res = new Ciphertext[size];
    ParallelFor(size, [&](int i) {
      res[i] = encryptor_->Encrypt(encoder(i));
    });
    sw.PrintWithMills("encrypts");

    return res;
  }

  template<typename T>
  inline void decrypt(const Ciphertext &ct, T *out, std::function<void(const Plaintext &, T *)> decoder) {
    Plaintext pt;
    decryptor_->Decrypt(ct, &pt);
    decoder(pt, out);
  }

  template<typename T>
  inline void decrypts(const Ciphertext *cts,
                       size_t size,
                       T *out,
                       std::function<void(const Plaintext &, T *)> decoder) {
    sw.Mark("decrypts");
    ParallelFor(size, [&](int i) {
      this->decrypt(cts[i], &out[i], decoder);
    });
    sw.PrintWithMills("decrypts");
  }

  inline Ciphertext *op(const Ciphertext &a,
                        const Ciphertext &b,
                        std::function<void(Ciphertext *, const Ciphertext &)> op_f);
  inline Ciphertext *op(const Ciphertext *a,
                        const Ciphertext *b,
                        size_t size,
                        std::function<void(Ciphertext *, const Ciphertext &)> op_f, const std::string &op_name);
  inline void opInplace(Ciphertext *a, const Ciphertext &b, std::function<void(Ciphertext *, const Ciphertext &)> op_f);
  inline void opInplace(Ciphertext *a,
                        const Ciphertext *b,
                        size_t size,
                        std::function<void(Ciphertext *, const Ciphertext &)> op_f, const std::string &op_name);

  bool hasSecretKey() const;
  const std::shared_ptr<heu::lib::phe::PublicKey> &getPublicKey() const;
  const std::shared_ptr<heu::lib::phe::SecretKey> &getSecretKey() const;

  PheKit(yacl::ByteContainerView pk_buffer, int64_t scale = 1e6);

 public:
  PheKit(SchemaType schema_type, size_t key_size = 2048, int64_t scale = 1e6);
  PheKit(const char *pk_buffer, int64_t scale = 1e6);

  Ciphertext *encrypt(double m);
  Ciphertext *encrypts(double *ms, size_t size);

  double decrypt(const Ciphertext &ct);
  void decrypts(const Ciphertext *cts, size_t size, double *out);
  double *decrypts(const Ciphertext *cts, size_t size);

  Ciphertext *encryptPair(double m1, double m2);
  Ciphertext *encryptPairs(double *ms1, double *ms2, size_t size);

  void decryptPair(const Ciphertext &ct, double *out);
  double *decryptPair_(const Ciphertext &ct);
  void decryptPairs(const Ciphertext *cts, size_t size, double *out);
  double *decryptPairs(const Ciphertext *cts, size_t size);

  Ciphertext *add(const Ciphertext &ct1, const Ciphertext &ct2);
  Ciphertext *adds(const Ciphertext *cts1, const Ciphertext *cts2, size_t size);
  void addInplace(Ciphertext &ct1, const Ciphertext &ct2);
  void addInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size);

  Ciphertext *sub(const Ciphertext &ct1, const Ciphertext &ct2);
  Ciphertext *subs(const Ciphertext *cts1, const Ciphertext *cts2, size_t size);
  void subInplace(Ciphertext &ct1, const Ciphertext &ct2);
  void subInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size);
};
