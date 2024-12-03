//
// Created by 2024 dterazhao.
//

#include <gtest/gtest.h>

#include <iostream>

#include "heu/phe_kit.h"

TEST(phe_kit, single_op) {
  StopWatch sw;

  sw.Mark("init");
  PheKit pheKit(1);
  sw.PrintWithMills("init");
  std::cout << pheKit.getPublicKey()->ToString() << std::endl;

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
}

TEST(phe_kit, pair_op) {
  StopWatch sw;

  sw.Mark("init");
  PheKit pheKit(SchemaType::OU);
  sw.PrintWithMills("init");
  std::cout << pheKit.getPublicKey()->ToString() << std::endl;

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
  auto res = pheKit.decryptPair(*ct);
  sw.PrintWithMills("decryptPair");
  std::cout << "real: [" << a1 + b1 + b1 << " " << a2 + b2 + b2 << "], res: [" << res[0] << " " << res[1] << "]"
            << std::endl;
}

TEST(phe_kit, batch_op) {
  PheKit pheKit(SchemaType::OU);
  std::cout << pheKit.getPublicKey()->ToString() << std::endl;

  const int len = 100000, freq = 4;
  double *ms1 = new double[len];
  double *ms2 = new double[len];
  for (int i = 0; i < len; ++i) {
    ms1[i] = i * 10 + i / 10;
    ms2[i] = i * 100 + (i + 3) / 10;
  }

  auto ct1 = pheKit.encrypt(ms1, len);
  auto ct2 = pheKit.encrypt(ms2, len);
  auto ct = pheKit.add(ct1, ct2, len);
  pheKit.addInplace(ct, ct2, len);
  auto res = pheKit.decrypt(ct, len);

  for (int i = 0; i < len; ++i) {
    if (i % (len / freq) == 0) {
      std::cout << "real: " << ms1[i] + ms2[i] + ms2[i] << ", res: " << res[i] << std::endl;
    }
  }

}

TEST(phe_kit, batch_pair_op) {
  PheKit pheKit(SchemaType::OU);
  std::cout << pheKit.getPublicKey()->ToString() << std::endl;

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

  auto ct1 = pheKit.encryptPair(ms11, ms12, len);
  auto ct2 = pheKit.encryptPair(ms21, ms22, len);
  auto ct = pheKit.add(ct1, ct2, len);
  pheKit.addInplace(ct, ct2, len);
  auto res = pheKit.decryptPair(ct, len);

  for (int i = 0; i < len; ++i) {
    if (i % (len / freq) == 0) {
      std::cout << "real: [" << ms11[i] + 2 * ms21[i] << " " << ms12[i] + 2 * ms22[i] << "], res: [" << res[i][0] << " "
                << res[i][1] << "]" << std::endl;
    }
  }

}
