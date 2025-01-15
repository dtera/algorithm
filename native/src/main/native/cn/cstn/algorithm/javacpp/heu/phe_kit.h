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

    void init(const std::shared_ptr<heu::lib::phe::HeKitPublicBase> &he_kit, int64_t scale);

    template<typename... ARGS>
    Ciphertext *encrypt(std::function<Plaintext(ARGS...)> encoder, ARGS... args) {
        auto res = new Ciphertext();
        *res = encryptor_->Encrypt(encoder(std::forward<ARGS>(args)...));
        return res;
    }

    Ciphertext *encrypts(const size_t size, const std::function<void(int, Ciphertext *)> &do_encrypt,
                         const std::string &mark = "encrypts", const int repeats = 1) {
        sw.Mark(mark);
        const auto res = new Ciphertext[size * repeats];
        ParallelFor(size, [&](const int i) {
            do_encrypt(i, res);
        });
        sw.Stop();

        return res;
    }

    Ciphertext *encrypts(const size_t size, const std::function<Plaintext(int)> &encoder,
                         const std::string &mark = "encrypts") {
        return encrypts(size, [&](const int i, Ciphertext *res) {
            res[i] = encryptor_->Encrypt(encoder(i));
        }, mark);
    }

    [[nodiscard]] Ciphertext *encryptPairUnpack(double m1, double m2) const;

    Ciphertext *encryptPairsUnpack(const double *ms1, const double *ms2, size_t size,
                                   const std::string &mark = "encryptPairsUnpack");

    template<typename T>
    void decrypt(const Ciphertext &ct, T *out, std::function<void(const Plaintext &, T *)> decoder) {
        Plaintext pt;
        decryptor_->Decrypt(ct, &pt);
        decoder(pt, out);
    }

    template<typename T>
    void decrypts(const Ciphertext *cts,
                  size_t size,
                  T *out,
                  std::function<void(const Plaintext &, T *)> decoder,
                  const std::string &mark = "decrypts") {
        sw.Mark(mark);
        ParallelFor(size, [&](int i) {
            this->decrypt(cts[i], &out[i], decoder);
        });
        sw.Stop();
    }

    static inline Ciphertext *op(const Ciphertext &a,
                                 const Ciphertext &b,
                                 const std::function<void(Ciphertext *, const Ciphertext &)> &op_f);

    inline Ciphertext *op(const Ciphertext *a,
                          const Ciphertext *b,
                          size_t size,
                          const std::function<void(Ciphertext *, const Ciphertext &)> &op_f,
                          const std::string &mark);

    static inline void opInplace(Ciphertext *a, const Ciphertext &b,
                                 const std::function<void(Ciphertext *, const Ciphertext &)> &op_f);

    inline void opInplace(Ciphertext *a,
                          const Ciphertext *b,
                          size_t size,
                          const std::function<void(Ciphertext *, const Ciphertext &)> &op_f,
                          const std::string &mark);

    [[nodiscard]] bool hasSecretKey() const;

    [[nodiscard]] const std::shared_ptr<heu::lib::phe::PublicKey> &getPublicKey() const;

    [[nodiscard]] const std::shared_ptr<heu::lib::phe::SecretKey> &getSecretKey() const;

    explicit PheKit(yacl::ByteContainerView pk_buffer, int64_t scale = 1e6, bool register_ec_lib = false);

public:
    static const std::string empty;
    static const std::string ed25519;
    static const std::string curve25519;
    static const std::string sm2;
    static const std::string secp256k1;
    static const std::string secp192r1;
    static const std::string secp256r1;
    static const std::string fourq;

    explicit PheKit(SchemaType schema, size_t key_size = 2048, int64_t scale = 1e6, const std::string &curve_name = "",
                    bool register_ec_lib = false);

    explicit PheKit(SchemaType schema, const std::string &curve_name, bool register_ec_lib = false);

    explicit PheKit(const std::string &pk_buffer, int64_t scale = 1e6, bool register_ec_lib = false);

    [[nodiscard]] std::string pubKey() const;

    Ciphertext *encrypt(double m);

    Ciphertext *encrypts(const double *ms, size_t size, const std::string &mark = "encrypts");

    double decrypt(const Ciphertext &ct);

    void decrypts(const Ciphertext *cts, size_t size, double *out, const std::string &mark = "decrypts");

    double *decrypts(const Ciphertext *cts, size_t size, const std::string &mark = "decrypts");

    Ciphertext *encryptPair(double m1, double m2, bool unpack = false);

    Ciphertext *encryptPairs(const double *ms1, const double *ms2, size_t size, bool unpack = false,
                             const std::string &mark = "encryptPairs");

    void decryptPair(const Ciphertext &ct, double *out, bool unpack = false);

    double *decryptPair_(const Ciphertext &ct, bool unpack = false);

    void decryptPairs(const Ciphertext *cts, size_t size, double *out, bool unpack = false,
                      const std::string &mark = "decryptPairs");

    double *decryptPairs(const Ciphertext *cts, size_t size, bool unpack = false,
                         const std::string &mark = "decryptPairs");

    [[nodiscard]] Ciphertext *add(const Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    Ciphertext *adds(const Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "adds");

    void addInplace(Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    void addInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "addInplaces");

    [[nodiscard]] Ciphertext *sub(const Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    Ciphertext *subs(const Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "subs");

    void subInplace(Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    void subInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "subInplaces");

    void prettyPrint(uint8_t time_unit = MILLISECONDS) const;
};

void deletePheKit(const PheKit *pheKit);

void deleteCiphertext(const Ciphertext *ciphertext);

void deleteCiphertexts(const Ciphertext *ciphertext);
