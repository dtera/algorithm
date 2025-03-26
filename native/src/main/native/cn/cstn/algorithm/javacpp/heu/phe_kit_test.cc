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

void ciphers_serial_deserialize(const SchemaType &schema, const int len = 100000, const std::string &curve_name = "") {
    PheKit pheKit(schema, curve_name);

    auto *ms1 = new double[len];
    auto *ms2 = new double[len];
    for (int i = 0; i < len; ++i) {
        ms1[i] = i + static_cast<double>(i) / 10;
        ms2[i] = i + static_cast<double>(i + 3) / 10;
    }
    const auto c1 = pheKit.encrypts(ms1, len, "[ct1]encrypts");
    const auto c2 = pheKit.encrypts(ms2, len, "[ct2]encrypts");
    const auto bt1 = ciphers2Bytes(c1, len);
    const auto bt2 = ciphers2Bytes(c2, len);
    const auto ct1 = bytes2Ciphers(*bt1, len);
    const auto ct2 = bytes2Ciphers(*bt2, len);

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

    if (schema != SchemaType::ElGamal) {
        sw.Mark("mul");
        const auto mulCt = pheKit.mul(*ct1, b);
        sw.Stop();
        sw.Mark("mulInplace");
        pheKit.mulInplace(*mulCt, b);
        sw.Stop();
        sw.Mark("[mul]decrypt");
        res = pheKit.decrypt(*mulCt);
        sw.Stop();
        std::cout << "[mul]real: " << a * b * b << ", res: " << res << std::endl;

        sw.Mark("neg");
        const auto negCt = pheKit.negate(*mulCt);
        sw.Stop();
        sw.Mark("[neg]decrypt");
        res = pheKit.decrypt(*negCt);
        sw.Stop();
        std::cout << "[neg]real: " << -a * b * b << ", res: " << res << std::endl;
        deleteCiphertext(mulCt);
        deleteCiphertext(negCt);
    }
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

    if (schema != SchemaType::ElGamal) {
        const auto mulCt = pheKit.muls(ct1, ms2, len);
        pheKit.mulInplaces(mulCt, ms2, len);
        res = pheKit.decrypts(mulCt, len, "[mul_p]decrypts");
        for (int i = 0; i < len; ++i) {
            if (constexpr int freq = 4; i % (len / freq) == 0) {
                std::cout << "[mul_p]real: " << ms1[i] * ms2[i] * ms2[i] << ", res: " << res[i] << std::endl;
            }
        }
        pheKit.negateInplaces(mulCt, len);
        res = pheKit.decrypts(mulCt, len, "[negate]decrypts");
        for (int i = 0; i < len; ++i) {
            if (constexpr int freq = 4; i % (len / freq) == 0) {
                std::cout << "[negate]real: " << -ms1[i] * ms2[i] * ms2[i] << ", res: " << res[i] << std::endl;
            }
        }
        deleteCiphertexts(mulCt);
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

void histogram(const SchemaType &schema, const int n = 100000, const int num_features = 1000, const int num_bins = 40) {
    PheKit pheKit(schema);

    std::mt19937 rng(std::random_device{}());
    std::uniform_real_distribution dist(0.0, 100.01);
    auto *grads = new double[n];
    for (int i = 0; i < n; ++i) {
        grads[i] = dist(rng);
    }

    const auto gradCts = pheKit.encrypts(grads, n);
    const auto total_bins = num_bins * num_features;
    auto [indexes, index_size] = genIndexes(n, num_features, num_bins);
    const auto real = pheKit.histogram(grads, indexes, index_size, num_bins, num_features);
    const auto resCts = pheKit.histogram(gradCts, indexes, index_size, num_bins, num_features);
    const auto res = pheKit.decrypts(resCts, total_bins);
    std::cout << "[histogram]: ";
    for (int i = 0; i < total_bins; ++i) {
        if (constexpr int freq = 5; i % (total_bins / freq) == 0) {
            std::cout << "[" << real[i] << "," << res[i] << "] ";
        }
    }
    std::cout << std::endl;

    pheKit.prettyPrint();

    delete [] indexes;
    delete [] index_size;
    deleteCiphertexts(gradCts);
    //deleteCiphertexts(resCts);
}

TEST(phe_kit, ou_pub_key_t) {
    pub_key_t(SchemaType::OU);
}

TEST(phe_kit, ou_ciphers_serial_deserialize) {
    ciphers_serial_deserialize(SchemaType::OU, 10000);
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

TEST(phe_kit, ou_histogram) {
    histogram(SchemaType::OU, 100000, 1000, 40);
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
