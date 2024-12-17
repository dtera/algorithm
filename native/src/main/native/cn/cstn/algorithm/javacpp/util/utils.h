// Copyright 2024 dterazhao.
#pragma once

#include <cassert>
#include <chrono>
#include <cstdint>
#include <functional>
#include <iostream>
#include <iterator>
#include <mutex>
#include <sstream>
#include <string>

#include "fmt/format.h"
#include "omp.h"

#define TIME_STAT(statments, name)                                             \
  {                                                                            \
    auto start = std::chrono::high_resolution_clock::now();                    \
    statments;                                                                 \
    auto end = std::chrono::high_resolution_clock::now();                      \
    double cost = 1.0 * std::chrono::duration_cast<std::chrono::microseconds>( \
                            end - start)                                       \
                            .count();                                          \
    std::cout << #name << " costs: " << cost / 1000.0 << " ms." << std::endl;  \
  }

/**
 * OpenMP schedule
 */
struct Sched {
    enum {
        kAuto,
        kDynamic,
        kStatic,
        kGuided,
    } sched;

    size_t chunk{0};

    [[maybe_unused]] Sched static Auto() { return Sched{kAuto}; }

    [[maybe_unused]] Sched static Dyn(const size_t n = 0) { return Sched{kDynamic, n}; }

    Sched static Static(const size_t n = 0) { return Sched{kStatic, n}; }

    [[maybe_unused]] Sched static Guided() { return Sched{kGuided}; }
};

/*!
 * \brief OMP Exception class catches, saves and rethrows exception from OMP
 * blocks
 */
class OMPException {
    // exception_ptr member to store the exception
    std::exception_ptr omp_exception_;
    // mutex to be acquired during catch to set the exception_ptr
    std::mutex mutex_;

public:
    /*!
     * \brief Parallel OMP blocks should be placed within Run to save exception
     */
    template<typename Function, typename... Parameters>
    void Run(Function f, Parameters... params) {
        try {
            f(params...);
        } catch ([[maybe_unused]] std::runtime_error &ex) {
            std::lock_guard lock(mutex_);
            if (!omp_exception_) {
                omp_exception_ = std::current_exception();
            }
        } catch ([[maybe_unused]] std::exception &ex) {
            std::lock_guard lock(mutex_);
            if (!omp_exception_) {
                omp_exception_ = std::current_exception();
            }
        }
    }

    /*!
     * \brief should be called from the main thread to rethrow the exception
     */
    void Rethrow() const {
        if (this->omp_exception_) std::rethrow_exception(this->omp_exception_);
    }
};

template<typename Index, typename Func>
void ParallelFor(Index size, [[maybe_unused]] const int32_t n_threads, const Sched sched,
                 Func fn) {
#if defined(_MSC_VER)
  // msvc doesn't support unsigned integer as openmp index.
  using OmpInd =
      std::conditional_t<std::is_signed<Index>::value, Index, omp_ulong>;
#else
    using OmpInd = Index;
#endif
    auto length = static_cast<OmpInd>(size);
    assert(n_threads >= 1);

    OMPException exc;
    switch (sched.sched) {
        case Sched::kAuto: {
#pragma omp parallel for num_threads(n_threads)
            for (OmpInd i = 0; i < length; ++i) {
                exc.Run(fn, i);
            }
            break;
        }
        case Sched::kDynamic: {
            if (sched.chunk == 0) {
#pragma omp parallel for num_threads(n_threads) schedule(dynamic)
                for (OmpInd i = 0; i < length; ++i) {
                    exc.Run(fn, i);
                }
            } else {
#pragma omp parallel for num_threads(n_threads) schedule(dynamic, sched.chunk)
                for (OmpInd i = 0; i < length; ++i) {
                    exc.Run(fn, i);
                }
            }
            break;
        }
        case Sched::kStatic: {
            if (sched.chunk == 0) {
#pragma omp parallel for num_threads(n_threads) schedule(static)
                for (OmpInd i = 0; i < length; ++i) {
                    exc.Run(fn, i);
                }
            } else {
#pragma omp parallel for num_threads(n_threads) schedule(static, sched.chunk)
                for (OmpInd i = 0; i < length; ++i) {
                    exc.Run(fn, i);
                }
            }
            break;
        }
        case Sched::kGuided: {
#pragma omp parallel for num_threads(n_threads) schedule(guided)
            for (OmpInd i = 0; i < length; ++i) {
                exc.Run(fn, i);
            }
            break;
        }
    }
    exc.Rethrow();
}

template<typename Index, typename Func>
void ParallelFor(Index size, int32_t n_threads, Func fn) {
    ParallelFor(size, n_threads, Sched::Static(), fn);
}

template<typename Index, typename Func>
void ParallelFor(Index size, Sched sched, Func fn) {
    ParallelFor(size, omp_get_num_procs(), sched, fn);
}

template<typename Index, typename Func>
void ParallelFor(Index size, Func fn) {
    ParallelFor(size, omp_get_num_procs(), fn);
}

template<typename Index, typename Func>
void ParallelFor_(Index size, Func fn) {
#pragma omp parallel num_threads(1)
#pragma omp for
    for ([[maybe_unused]] int i = 0; i < size; ++i) {
        fn(i);
    }
}

#define CHECK_BINARY_OP(a, b, op, msg) \
  if (!(a op b)) std::cerr << "Check failed: " << #msg << std::endl;
#define CHECK_LT(a, b) CHECK_BINARY_OP(a, b, <, a < b is necessary)
#define CHECK_GE(a, b) CHECK_BINARY_OP(a, b, >=, a >= b is necessary)

// Represent simple range of indexes [begin, end)
// Inspired by tbb::blocked_range
class Range1d {
public:
    Range1d(const size_t begin, const size_t end)
        : begin_(begin),
          end_(end) { CHECK_LT(begin, end) }

    [[nodiscard]] size_t begin() const {
        // NOLINT
        return begin_;
    }

    [[nodiscard]] size_t end() const {
        // NOLINT
        return end_;
    }

private:
    size_t begin_;
    size_t end_;
};

// Split 2d space to balanced blocks
// Implementation of the class is inspired by tbb::blocked_range2d
// However, TBB provides only (n x m) 2d range (matrix) separated by blocks.
// Example: [ 1,2,3 ] [ 4,5,6 ] [ 7,8,9 ] But the class is able to work with
// different sizes in each 'row'. Example: [ 1,2 ] [ 3,4,5,6 ] [ 7,8,9] If
// grain_size is 2: It produces following blocks: [1,2], [3,4], [5,6], [7,8],
// [9] The class helps to process data in several tree nodes (non-balanced
// usually) in parallel Using nested parallelism (by nodes and by data in each
// node) it helps  to improve CPU resources utilization
class BlockedSpace2d {
public:
    // Example of space:
    // [ 1,2 ]
    // [ 3,4,5,6 ]
    // [ 7,8,9]
    // BlockedSpace2d will create following blocks (tasks) if grain_size=2:
    // 1-block: first_dimension = 0, range of indexes in a 'row' = [0,2) (includes
    // [1,2] values) 2-block: first_dimension = 1, range of indexes in a 'row' =
    // [0,2) (includes [3,4] values) 3-block: first_dimension = 1, range of
    // indexes in a 'row' = [2,4) (includes [5,6] values) 4-block: first_dimension
    // = 2, range of indexes in a 'row' = [0,2) (includes [7,8] values) 5-block:
    // first_dimension = 2, range of indexes in a 'row' = [2,3) (includes [9]
    // values) Arguments: dim1 - size of the first dimension in the space
    // getter_size_dim2 - functor to get the second dimensions for each 'row' by
    // row-index grain_size - max size of produced blocks
    template<typename Func>
    BlockedSpace2d(const size_t dim1, Func getter_size_dim2, const size_t grain_size) {
        for (size_t i = 0; i < dim1; ++i) {
            const size_t size = getter_size_dim2(i);
            const size_t n_blocks = size / grain_size + !!(size % grain_size);
            for (size_t iblock = 0; iblock < n_blocks; ++iblock) {
                const size_t begin = iblock * grain_size;
                const size_t end = std::min(begin + grain_size, size);
                AddBlock(i, begin, end);
            }
        }
    }

    // Amount of blocks(tasks) in a space
    [[nodiscard]] size_t Size() const { return ranges_.size(); }

    // get index of the first dimension of i-th block(task)
    [[nodiscard]] size_t GetFirstDimension(const size_t i) const {
        CHECK_LT(i, first_dimension_.size())
        return first_dimension_[i];
    }

    // get a range of indexes for the second dimension of i-th block(task)
    [[nodiscard]] Range1d GetRange(const size_t i) const {
        CHECK_LT(i, ranges_.size())
        return ranges_[i];
    }

private:
    void AddBlock(const size_t first_dimension, size_t begin, size_t end) {
        first_dimension_.push_back(first_dimension);
        ranges_.emplace_back(begin, end);
    }

    std::vector<Range1d> ranges_;
    std::vector<size_t> first_dimension_;
};

// Wrapper to implement nested parallelism with simple omp parallel for
template<typename Func>
void ParallelFor2d(const BlockedSpace2d &space, const int n_threads, Func func) {
    const size_t num_blocks_in_space = space.Size();
    CHECK_GE(n_threads, 1)

    OMPException exc;
#pragma omp parallel num_threads(n_threads)
    {
        exc.Run([&] {
            const size_t tid = omp_get_thread_num();
            const size_t chunk_size =
                    num_blocks_in_space / n_threads + !!(num_blocks_in_space % n_threads);

            const size_t begin = chunk_size * tid;
            const size_t end = std::min(begin + chunk_size, num_blocks_in_space);
            for (auto i = begin; i < end; i++) {
                func(space.GetFirstDimension(i), space.GetRange(i));
            }
        });
    }
    exc.Rethrow();
}

template<typename T>
struct fmt::formatter<std::vector<T> > : formatter<std::string> {
    static auto format(std::vector<T> sc, format_context &ctx) -> decltype(ctx.out()) {
        std::ostringstream os;
        std::copy(sc.begin(), sc.end(), std::ostream_iterator<T>(os, ", "));
        return format_to(ctx.out(), "[{}]", os.str().substr(0, os.str().length() - 2));
    }
};
