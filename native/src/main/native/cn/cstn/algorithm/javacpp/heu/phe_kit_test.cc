//
// Created by 2024 dterazhao.
//

#include <gtest/gtest.h>

#include <iostream>

#include "heu/phe_kit.h"

TEST(phe_kit, pub_key_t) {
  StopWatch sw;

  sw.Mark("init");
  PheKit pheKit(SchemaType::OU);
  auto pk = pheKit.pubKey();
  PheKit dpheKit(pk);
  sw.PrintWithMills("init");

  double a = 2.36, b = 5.12;
  sw.Mark("encrypt");
  auto ct1 = dpheKit.encrypt(a);
  auto ct2 = dpheKit.encrypt(b);
  sw.PrintWithMills("encrypt");
  sw.Mark("add");
  auto ct = dpheKit.add(*ct1, *ct2);
  sw.PrintWithMills("add");
  sw.Mark("addInplace");
  dpheKit.addInplace(*ct, *ct2);
  sw.PrintWithMills("addInplace");
  sw.Mark("decrypt");
  auto res = pheKit.decrypt(*ct);
  sw.PrintWithMills("decrypt");
  std::cout << "real: " << a + b + b << ", res: " << res << std::endl;
  deleteCiphertext(ct1);
  deleteCiphertext(ct2);
  deleteCiphertext(ct);
}

TEST(phe_kit, single_op) {
  StopWatch sw;

  sw.Mark("init");
  PheKit pheKit(SchemaType::ElGamal);
  sw.PrintWithMills("init");

  double a = 2.36, b = 5.12;
  sw.Mark("encrypt");
  auto ct1 = pheKit.encrypt(a);
  auto ct2 = pheKit.encrypt(b);
  sw.PrintWithMills("encrypt");
  sw.Mark("add");
  auto ct = pheKit.add(*ct1, *ct2);
  sw.PrintWithMills("add");
  sw.Mark("addInplace");
  pheKit.addInplace(*ct, *ct2);
  sw.PrintWithMills("addInplace");
  sw.Mark("decrypt");
  auto res = pheKit.decrypt(*ct);
  sw.PrintWithMills("decrypt");
  std::cout << "real: " << a + b + b << ", res: " << res << std::endl;
  deleteCiphertext(ct1);
  deleteCiphertext(ct2);
  deleteCiphertext(ct);
}

TEST(phe_kit, pair_op) {
  StopWatch sw;

  sw.Mark("init");
  PheKit pheKit(SchemaType::OU);
  sw.PrintWithMills("init");

  double a1 = 2.36, a2 = 5.12, b1 = 3.12, b2 = 7.45;
  sw.Mark("encryptPair");
  auto ct1 = pheKit.encryptPair(a1, a2);
  auto ct2 = pheKit.encryptPair(b1, b2);
  sw.PrintWithMills("encryptPair");
  sw.Mark("add");
  auto ct = pheKit.add(*ct1, *ct2);
  sw.PrintWithMills("add");
  sw.Mark("addInplace");
  pheKit.addInplace(*ct, *ct2);
  sw.PrintWithMills("addInplace");
  sw.Mark("decryptPair");
  auto res = pheKit.decryptPair_(*ct);
  sw.PrintWithMills("decryptPair");
  std::cout << "real: [" << a1 + b1 + b1 << " " << a2 + b2 + b2 << "], res: [" << res[0] << " " << res[1] << "]"
            << std::endl;
  deleteCiphertext(ct1);
  deleteCiphertext(ct2);
  deleteCiphertext(ct);
}

TEST(phe_kit, batch_op) {
  PheKit pheKit(SchemaType::OU);

  const int len = 100000, freq = 4;
  double *ms1 = new double[len];
  double *ms2 = new double[len];
  for (int i = 0; i < len; ++i) {
    ms1[i] = i * 10 + i / 10;
    ms2[i] = i * 100 + (i + 3) / 10;
  }

  auto ct1 = pheKit.encrypts(ms1, len);
  auto ct2 = pheKit.encrypts(ms2, len);
  auto ct = pheKit.adds(ct1, ct2, len);
  pheKit.addInplaces(ct, ct2, len);
  auto res = pheKit.decrypts(ct, len);

  for (int i = 0; i < len; ++i) {
    if (i % (len / freq) == 0) {
      std::cout << "real: " << ms1[i] + ms2[i] + ms2[i] << ", res: " << res[i] << std::endl;
    }
  }
  deleteCiphertexts(ct1);
  deleteCiphertexts(ct2);
  deleteCiphertexts(ct);

}

TEST(phe_kit, batch_pair_op) {
  PheKit pheKit(SchemaType::OU);

  const int len = 100000, freq = 3;
  double *ms11 = new double[len];
  double *ms12 = new double[len];
  double *ms21 = new double[len];
  double *ms22 = new double[len];
  for (int i = 0; i < len; ++i) {
    ms11[i] = i * 10 + i / 10;
    ms12[i] = i * 10 + i / 5;
    ms21[i] = i * 100 + (i + 3) / 10;
    ms22[i] = i * 100 + (i + 3) / 5;
  }

  auto ct1 = pheKit.encryptPairs(ms11, ms12, len);
  auto ct2 = pheKit.encryptPairs(ms21, ms22, len);
  auto ct = pheKit.adds(ct1, ct2, len);
  pheKit.addInplaces(ct, ct2, len);
  auto res = pheKit.decryptPairs(ct, len);

  for (int i = 0; i < len; ++i) {
    if (i % (len / freq) == 0) {
      std::cout << "real: [" << ms11[i] + 2 * ms21[i] << " " << ms12[i] + 2 * ms22[i] << "], res: [" << res[i] << " "
                << res[i + len] << "]" << std::endl;
    }
  }
  deleteCiphertexts(ct1);
  deleteCiphertexts(ct2);
  deleteCiphertexts(ct);

}
