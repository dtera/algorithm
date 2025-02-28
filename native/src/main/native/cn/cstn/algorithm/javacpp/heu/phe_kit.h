//
// Created by 2024 dterazhao.
//

#pragma once

#include <type_traits>

#include "heu/library/phe/encoding/encoding.h"

#include "he_types.h"
#include "util/stopwatch.hpp"
#include "util/utils.h"

//**************************************************PheKit Begin**************************************************
class PheKit {
    std::shared_ptr<heu::lib::phe::HeKit> he_kit_;
    std::shared_ptr<heu::lib::phe::DestinationHeKit> dhe_kit_;
    std::shared_ptr<heu::lib::phe::Encryptor> encryptor_;
    std::shared_ptr<heu::lib::phe::Decryptor> decryptor_;
    std::shared_ptr<heu::lib::phe::Evaluator> evaluator_;
    std::unordered_map<int, std::shared_ptr<heu::lib::phe::PlainEncoder> > encoders_;
    std::unordered_map<int, std::shared_ptr<heu::lib::phe::BatchEncoder> > batch_encoders_;
    int scale_cnt_ = 10;
    bool has_secret_key = true;
    StopWatch sw;

protected:
    std::function<Plaintext(double)> encoder_f;
    std::function<void(const Plaintext &, double *, int)> decoder_f;
    std::function<Plaintext(double, double)> batch_encoder_f;
    std::function<void(const Plaintext &, double *, int)> batch_decoder_f;
    std::function<void(Ciphertext *, const Ciphertext &)> add_f;
    std::function<void(Ciphertext *, const Ciphertext &)> sub_f;
    std::function<void(Ciphertext *, const Plaintext &)> add_p_f;
    std::function<void(Ciphertext *, const Plaintext &)> sub_p_f;
    std::function<void(Ciphertext *, const Plaintext &)> mul_p_f;

    explicit PheKit(const yacl::ByteContainerView &pk_buffer,
                    const std::unique_ptr<yacl::ByteContainerView> &sk_buffer = nullptr,
                    int64_t scale = 1e6, int scale_cnt = 10, bool register_ec_lib = false);

    template<typename T>
    void init(const std::shared_ptr<T> &kit, int64_t scale);

    static std::unique_ptr<yacl::ByteContainerView> getBuffer(const std::string &s_buffer);

    [[nodiscard]] bool hasSecretKey() const;

    [[nodiscard]] const std::shared_ptr<heu::lib::phe::PublicKey> &getPublicKey() const;

    [[nodiscard]] const std::shared_ptr<heu::lib::phe::SecretKey> &getSecretKey() const;

    template<typename... ARGS>
    Ciphertext *encrypt(std::function<Plaintext(ARGS...)> encoder, ARGS... args);

    Ciphertext *encrypts(size_t size, const std::function<void(int, Ciphertext *)> &do_encrypt,
                         const std::string &mark = "encrypts", int repeats = 1);

    Ciphertext *encrypts(size_t size, const std::function<Plaintext(int)> &encoder,
                         const std::string &mark = "encrypts");

    [[nodiscard]] Ciphertext *encryptPairUnpack(double m1, double m2) const;

    Ciphertext *encryptPairsUnpack(const double *ms1, const double *ms2, size_t size,
                                   const std::string &mark = "encryptPairsUnpack");

    template<typename T=double>
    void decrypt(const Ciphertext &ct, T *out, std::function<void(const Plaintext &, T *, int)> decoder);

    template<typename T=double>
    void decrypts(const Ciphertext *cts,
                  size_t size,
                  T *out,
                  std::function<void(const Plaintext &, T *, int)> decoder,
                  const std::string &mark = "decrypts");

    template<typename T=Ciphertext, int rescale_updater = 0>
    static Ciphertext *op(const Ciphertext &a,
                          const T &b,
                          const std::function<void(Ciphertext *, const T &)>
                          &op_f);

    template<typename T=Ciphertext, int rescale_updater = 0>
    Ciphertext *op(const Ciphertext *a,
                   const T *b,
                   size_t size,
                   const std::function<void(Ciphertext *, const T &)> &op_f,
                   const std::string &mark);

    template<typename T=Ciphertext, int rescale_updater = 0>
    static void opInplace(Ciphertext *a, const T &b,
                          const std::function<void(Ciphertext *, const T &)>
                          &op_f);

    template<typename T=Ciphertext, int rescale_updater = 0>
    void opInplace(Ciphertext *a,
                   const T *b,
                   size_t size,
                   const std::function<void(Ciphertext *, const T &)> &op_f,
                   const std::string &mark);

    template<int rescale_updater = 0>
    Ciphertext *op_(const Ciphertext *a,
                    const double *b,
                    size_t size,
                    const std::function<void(Ciphertext *, const Plaintext &)> &op_f,
                    const std::string &mark);

    template<int rescale_updater = 0>
    void opInplace_(Ciphertext *a,
                    const double *b,
                    size_t size,
                    const std::function<void(Ciphertext *, const Plaintext &)> &op_f,
                    const std::string &mark);

public:
    static const std::string empty;
    static const std::string ed25519;
    static const std::string curve25519;
    static const std::string sm2;
    static const std::string secp256k1;
    static const std::string secp192r1;
    static const std::string secp256r1;
    static const std::string fourq;

    explicit PheKit(SchemaType schema, size_t key_size = 2048, int64_t scale = 1e6, int scale_cnt = 10,
                    const std::string &curve_name = "", bool register_ec_lib = false);

    explicit PheKit(SchemaType schema, const std::string &curve_name, bool register_ec_lib = false);

    explicit PheKit(const std::string &pk_buffer, const std::string &sk_buffer, int64_t scale = 1e6,
                    int scale_cnt = 10, bool register_ec_lib = false);

    explicit PheKit(const std::string &pk_buffer, int64_t scale = 1e6, int scale_cnt = 10,
                    bool register_ec_lib = false);

    [[nodiscard]] std::string pubKey() const;

    [[nodiscard]] std::string secretKey() const;

    Ciphertext *encrypt(double m);

    Ciphertext *encrypts(const double *ms, size_t size, const std::string &mark = "encrypts");

    Ciphertext *encryptPair(double m1, double m2, bool unpack = false);

    Ciphertext *encryptPairs(const double *ms1, const double *ms2, size_t size, bool unpack = false,
                             const std::string &mark = "encryptPairs");

    double decrypt(const Ciphertext &ct);

    void decrypts(const Ciphertext *cts, size_t size, double *out, const std::string &mark = "decrypts");

    double *decrypts(const Ciphertext *cts, size_t size, const std::string &mark = "decrypts");

    void decryptPair(const Ciphertext &ct, double *out, bool unpack = false);

    double *decryptPair_(const Ciphertext &ct, bool unpack = false);

    void decryptPairs(const Ciphertext *cts, size_t size, double *out, bool unpack = false,
                      const std::string &mark = "decryptPairs");

    double *decryptPairs(const Ciphertext *cts, size_t size, bool unpack = false,
                         const std::string &mark = "decryptPairs");

    [[nodiscard]] Ciphertext *add(const Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    [[nodiscard]] Ciphertext *add(const Ciphertext &ct1, double pt2, bool unpack = false);

    Ciphertext *adds(const Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "adds");

    Ciphertext *adds(const Ciphertext *cts1, const double *pts2, size_t size, const std::string &mark = "adds_p");

    void addInplace(Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    void addInplace(Ciphertext &ct1, double pt2, bool unpack = false);

    void addInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "addInplaces");

    void addInplaces(Ciphertext *cts1, const double *pts2, size_t size, const std::string &mark = "addInplaces_p");

    [[nodiscard]] Ciphertext *sub(const Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    [[nodiscard]] Ciphertext *sub(const Ciphertext &ct1, double pt2, bool unpack = false);

    Ciphertext *subs(const Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "subs");

    Ciphertext *subs(const Ciphertext *cts1, const double *pts2, size_t size, const std::string &mark = "subs_p");

    void subInplace(Ciphertext &ct1, const Ciphertext &ct2, bool unpack = false);

    void subInplace(Ciphertext &ct1, double pt2, bool unpack = false);

    void subInplaces(Ciphertext *cts1, const Ciphertext *cts2, size_t size, const std::string &mark = "subInplaces");

    void subInplaces(Ciphertext *cts1, const double *pts2, size_t size, const std::string &mark = "subInplaces_p");

    [[nodiscard]] Ciphertext *mul(const Ciphertext &ct1, double pt2, bool unpack = false);

    Ciphertext *muls(const Ciphertext *cts1, const double *pts2, size_t size, const std::string &mark = "muls_p");

    void mulInplace(Ciphertext &ct1, double pt2, bool unpack = false);

    void mulInplaces(Ciphertext *cts1, const double *pts2, size_t size, const std::string &mark = "mulInplaces_p");

    [[nodiscard]] Ciphertext *negate(const Ciphertext &ct) const;

    Ciphertext *negates(const Ciphertext *cts, size_t size, const std::string &mark = "negates");

    void negateInplace(const Ciphertext &ct) const;

    void negateInplaces(const Ciphertext *cts, size_t size, const std::string &mark = "negateInplaces");

    void prettyPrint(uint8_t time_unit = MILLISECONDS) const;
};

//**************************************************PheKit End****************************************************

//**************************************************Global Begin**************************************************
void deletePheKit(const PheKit *pheKit);

void deleteCiphertext(const Ciphertext *ciphertext);

void deleteCiphertexts(const Ciphertext *ciphertext);

std::string cipher2Bytes(const Ciphertext &ciphertext);

Ciphertext *bytes2Cipher(const std::string &buffer);

HeBuffer *ciphers2Bytes(const Ciphertext *ciphertexts, size_t size);

Ciphertext *bytes2Ciphers(const HeBuffer &buffers, size_t size);

//**************************************************Global End****************************************************
