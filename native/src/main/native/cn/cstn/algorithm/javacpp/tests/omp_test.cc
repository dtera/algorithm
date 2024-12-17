//
// Created by 2024 dterazhao.
//

#include <gtest/gtest.h>

#include <iostream>

#include "util/stopwatch.hpp"
#include "util/utils.h"

TEST(omp, test1) {
    constexpr int iter = 10;
    const auto n_threads = omp_get_num_procs();
    std::cout << "n_threads: " << n_threads << std::endl;
    StopWatch sw(true);
    ParallelFor(iter, [&](const int i) {
        std::cout << i << std::endl;
        sleep(1);
    });
    sw.PrintWithSeconds();
}
