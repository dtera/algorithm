//
// Created by 2024 dterazhao.
//

#include <gtest/gtest.h>

#include <iostream>

#include "util/stopwatch.hpp"

#include "heu/phe_kit.h"

TEST(phe_kit, t1) {
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
  sw.Mark("decrypt");
  auto res = pheKit.decrypt(*ct);
  sw.PrintWithMills("decrypt");
  std::cout << "real: " << a + b << ", res: " << res << std::endl;
}
