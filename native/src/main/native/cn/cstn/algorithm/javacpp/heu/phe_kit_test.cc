//
// Created by 2024 dterazhao.
//

#include <gtest/gtest.h>

#include <iostream>

#include "heu/phe_kit.h"

void pub_key_t(const SchemaType &schema, const std::string &curve_name = "") {
    StopWatch sw;

    sw.Mark("init");
    PheKit pheKit(schema, curve_name);
    const auto pk = pheKit.pubKey();
    PheKit dpheKit(pk);
    sw.Stop();

    constexpr double a = 2.36, b = 5.12;
    sw.Mark("encrypt");
    const auto ct1 = dpheKit.encrypt(a);
    const auto ct2 = dpheKit.encrypt(b);
    sw.Stop();

    sw.Mark("add");
    const auto addCt = dpheKit.add(*ct1, *ct2);
    sw.Stop();
    sw.Mark("addInplace");
    dpheKit.addInplace(*addCt, *ct2);
    sw.Stop();
    sw.Mark("[add]decrypt");
    auto res = pheKit.decrypt(*addCt);
    sw.Stop();
    std::cout << "[add]real: " << a + b + b << ", res: " << res << std::endl;

    sw.Mark("sub");
    const auto subCt = dpheKit.sub(*addCt, *ct2);
    sw.Stop();
    sw.Mark("subInplace");
    dpheKit.subInplace(*subCt, *ct2);
    sw.Stop();
    sw.Mark("[sub]decrypt");
    res = pheKit.decrypt(*subCt);
    sw.Stop();
    std::cout << "[sub]real: " << a << ", res: " << res << std::endl;
    sw.PrettyPrint();

    deleteCiphertext(ct1);
    deleteCiphertext(ct2);
    deleteCiphertext(addCt);
    deleteCiphertext(subCt);
}

void single_op(const SchemaType &schema, const std::string &curve_name = "") {
    StopWatch sw;

    sw.Mark("init");
    PheKit pheKit(schema, curve_name);
    sw.Stop();

    constexpr double a = 2.36, b = 5.12;
    sw.Mark("encrypt");
    const auto ct1 = pheKit.encrypt(a);
    const auto ct2 = pheKit.encrypt(b);
    sw.Stop();

    sw.Mark("add");
    const auto addCt = pheKit.add(*ct1, *ct2);
    sw.Stop();
    sw.Mark("addInplace");
    pheKit.addInplace(*addCt, *ct2);
    sw.Stop();
    sw.Mark("[add]decrypt");
    auto res = pheKit.decrypt(*addCt);
    sw.Stop();
    std::cout << "[add]real: " << a + b + b << ", res: " << res << std::endl;

    sw.Mark("sub");
    const auto subCt = pheKit.sub(*addCt, *ct2);
    sw.Stop();
    sw.Mark("subInplace");
    pheKit.subInplace(*subCt, *ct2);
    sw.Stop();
    sw.Mark("[sub]decrypt");
    res = pheKit.decrypt(*subCt);
    sw.Stop();
    std::cout << "[sub]real: " << a << ", res: " << res << std::endl;
    sw.PrettyPrint();

    deleteCiphertext(ct1);
    deleteCiphertext(ct2);
    deleteCiphertext(addCt);
    deleteCiphertext(subCt);
}

void batch_op(const SchemaType &schema, const int len = 100000, const std::string &curve_name = "") {
    PheKit pheKit(schema, curve_name);

    auto *ms1 = new double[len];
    auto *ms2 = new double[len];
    for (int i = 0; i < len; ++i) {
        ms1[i] = i + static_cast<double>(i) / 10;
        ms2[i] = i + static_cast<double>(i + 3) / 10;
    }
    const auto ct1 = pheKit.encrypts(ms1, len, "[ct1]encrypts");
    const auto ct2 = pheKit.encrypts(ms2, len, "[ct2]encrypts");

    const auto addCt = pheKit.adds(ct1, ct2, len);
    pheKit.addInplaces(addCt, ct2, len);
    auto res = pheKit.decrypts(addCt, len, "[add]decrypts");
    for (int i = 0; i < len; ++i) {
        if (constexpr int freq = 4; i % (len / freq) == 0) {
            std::cout << "[add]real: " << ms1[i] + ms2[i] + ms2[i] << ", res: " << res[i] << std::endl;
        }
    }

    const auto subCt = pheKit.subs(addCt, ct2, len);
    pheKit.subInplaces(subCt, ct2, len);
    res = pheKit.decrypts(subCt, len, "[sub]decrypts");
    for (int i = 0; i < len; ++i) {
        if (constexpr int freq = 4; i % (len / freq) == 0) {
            std::cout << "[sub]real: " << ms1[i] << ", res: " << res[i] << std::endl;
        }
    }
    pheKit.prettyPrint();

    deleteCiphertexts(ct1);
    deleteCiphertexts(ct2);
    deleteCiphertexts(addCt);
    deleteCiphertexts(subCt);
}

void pair_op(const SchemaType &schema, const std::string &curve_name = "", const bool unpack = false) {
    StopWatch sw;

    sw.Mark("init");
    PheKit pheKit(schema, curve_name);
    sw.Stop();

    constexpr double a1 = 2.36, a2 = 5.12, b1 = 3.12, b2 = 7.45;
    sw.Mark("encryptPair");
    const auto ct1 = pheKit.encryptPair(a1, a2, unpack);
    const auto ct2 = pheKit.encryptPair(b1, b2, unpack);
    sw.Stop();

    sw.Mark("add");
    const auto addCt = pheKit.add(*ct1, *ct2, unpack);
    sw.Stop();
    sw.Mark("addInplace");
    pheKit.addInplace(*addCt, *ct2, unpack);
    sw.Stop();
    sw.Mark("[add]decryptPair");
    auto res = pheKit.decryptPair_(*addCt, unpack);
    sw.Stop();
    std::cout << "[add]real: [" << a1 + b1 + b1 << " " << a2 + b2 + b2 << "], res: [" << res[0] << " " << res[1] << "]"
            << std::endl;

    sw.Mark("sub");
    const auto subCt = pheKit.sub(*addCt, *ct2, unpack);
    sw.Stop();
    sw.Mark("subInplace");
    pheKit.subInplace(*subCt, *ct2, unpack);
    sw.Stop();
    sw.Mark("[sub]decrypt");
    res = pheKit.decryptPair_(*subCt, unpack);
    sw.Stop();
    std::cout << "[sub]real: [" << a1 << " " << a2 << "], res: [" << res[0] << " " << res[1] << "]" << std::endl;
    sw.PrettyPrint();

    deleteCiphertext(ct1);
    deleteCiphertext(ct2);
    deleteCiphertext(addCt);
    deleteCiphertext(subCt);
}

void batch_pair_op(const SchemaType &schema, const int len = 100000, const std::string &curve_name = "",
                   const bool unpack = false) {
    PheKit pheKit(schema, curve_name);

    auto *ms11 = new double[len];
    auto *ms12 = new double[len];
    auto *ms21 = new double[len];
    auto *ms22 = new double[len];
    for (int i = 0; i < len; ++i) {
        ms11[i] = i + static_cast<double>(i) / 10;
        ms12[i] = i + static_cast<double>(i) / 5;
        ms21[i] = i + static_cast<double>(i + 3) / 10;
        ms22[i] = i + static_cast<double>(i + 3) / 5;
    }
    const auto ct1 = pheKit.encryptPairs(ms11, ms12, len, unpack, "[ct1]encryptPairs");
    const auto ct2 = pheKit.encryptPairs(ms21, ms22, len, unpack, "[ct2]encryptPairs");

    const auto size = len * (unpack ? 2 : 1);
    const auto addCt = pheKit.adds(ct1, ct2, size);
    pheKit.addInplaces(addCt, ct2, size);
    auto res = pheKit.decryptPairs(addCt, len, unpack, "[add]decryptPairs");
    for (int i = 0; i < len; ++i) {
        if (constexpr int freq = 3; i % (len / freq) == 0) {
            std::cout << "[add]real: [" << ms11[i] + 2 * ms21[i] << " " << ms12[i] + 2 * ms22[i] << "], res: [" <<
                    res[i] << " " << res[i + len] << "]" << std::endl;
        }
    }

    const auto subCt = pheKit.subs(addCt, ct2, size);
    pheKit.subInplaces(subCt, ct2, size);
    res = pheKit.decryptPairs(subCt, len, unpack, "[sub]decryptPairs");
    for (int i = 0; i < len; ++i) {
        if (constexpr int freq = 4; i % (len / freq) == 0) {
            std::cout << "[sub]real: " << ms11[i] << " " << ms12[i] << "], res: [" << res[i] << " " << res[i + len] <<
                    "]" << std::endl;
        }
    }
    pheKit.prettyPrint();

    deleteCiphertexts(ct1);
    deleteCiphertexts(ct2);
    deleteCiphertexts(addCt);
    deleteCiphertexts(subCt);
}

TEST(phe_kit, ou_pub_key_t) {
    pub_key_t(SchemaType::OU);
}

TEST(phe_kit, ou_single_op) {
    single_op(SchemaType::OU);
}

TEST(phe_kit, ou_batch_op) {
    batch_op(SchemaType::OU);
}

TEST(phe_kit, ou_pair_op) {
    pair_op(SchemaType::OU);
}

TEST(phe_kit, ou_batch_pair_op) {
    batch_pair_op(SchemaType::OU);
}

TEST(phe_kit, elgamal_ed25519_pub_key_t) {
    pub_key_t(SchemaType::ElGamal, PheKit::ed25519);
}

TEST(phe_kit, elgamal_ed25519_single_op) {
    single_op(SchemaType::ElGamal, PheKit::ed25519);
}

TEST(phe_kit, elgamal_ed25519_batch_op) {
    batch_op(SchemaType::ElGamal, 10000, PheKit::ed25519);
}

TEST(phe_kit, elgamal_fourq_pub_key_t) {
    pub_key_t(SchemaType::ElGamal, PheKit::fourq);
}

TEST(phe_kit, elgamal_fourq_single_op) {
    single_op(SchemaType::ElGamal, PheKit::fourq);
}

TEST(phe_kit, elgamal_fourq_batch_op) {
    batch_op(SchemaType::ElGamal, 10000, PheKit::fourq);
}

TEST(phe_kit, elgamal_sm2_pub_key_t) {
    pub_key_t(SchemaType::ElGamal, PheKit::sm2);
}

TEST(phe_kit, elgamal_sm2_single_op) {
    single_op(SchemaType::ElGamal, PheKit::sm2);
}

TEST(phe_kit, elgamal_sm2_batch_op) {
    batch_op(SchemaType::ElGamal, 10000, PheKit::sm2);
}

TEST(phe_kit, elgamal_secp256k1_pub_key_t) {
    pub_key_t(SchemaType::ElGamal, PheKit::secp256k1);
}

TEST(phe_kit, elgamal_secp256k1_single_op) {
    single_op(SchemaType::ElGamal, PheKit::secp256k1);
}

TEST(phe_kit, elgamal_secp256k1_batch_op) {
    batch_op(SchemaType::ElGamal, 10000, PheKit::secp256k1);
}

TEST(phe_kit, elgamal_secp256r1_pub_key_t) {
    pub_key_t(SchemaType::ElGamal, PheKit::secp256r1);
}

TEST(phe_kit, elgamal_secp256r1_single_op) {
    single_op(SchemaType::ElGamal, PheKit::secp256r1);
}

TEST(phe_kit, elgamal_secp256r1_batch_op) {
    batch_op(SchemaType::ElGamal, 10000, PheKit::secp256r1);
}

TEST(phe_kit, elgamal_secp192r1_pub_key_t) {
    pub_key_t(SchemaType::ElGamal, PheKit::secp192r1);
}

TEST(phe_kit, elgamal_secp192r1_single_op) {
    single_op(SchemaType::ElGamal, PheKit::secp192r1);
}

TEST(phe_kit, elgamal_secp192r1_batch_op) {
    batch_op(SchemaType::ElGamal, 10000, PheKit::secp192r1);
}

TEST(phe_kit, elgamal_ed25519_pair_op) {
    pair_op(SchemaType::ElGamal, PheKit::ed25519, true);
}

TEST(phe_kit, elgamal_ed25519_batch_pair_op) {
    batch_pair_op(SchemaType::ElGamal, 10000, PheKit::ed25519, true);
}
