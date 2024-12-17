//
// Created by 2024 dterazhao.
//

#pragma once

#include <iostream>
#include <string>
#include <unordered_map>

class StopWatch {
public:
    explicit StopWatch(const bool mark_default = false, const bool print_on = true) : print_on(print_on) {
        if (mark_default) {
            Mark();
        }
    }

    // start a new mark
    void Mark(const std::string &mark = default_mark()) {
        mark_[mark] = std::chrono::high_resolution_clock::now();
    }

    // return a mark's cost , default in nanoseconds
    long long CountNano(const std::string &mark = default_mark()) {
        return CountLL<std::nano>(mark);
    }

    // return a mark's cost , default in microseconds
    long long CountMicro(const std::string &mark = default_mark()) {
        return CountLL<std::micro>(mark);
    }

    // return a mark's cost , default in milliseconds
    long long CountMilli(const std::string &mark = default_mark()) {
        return CountLL<std::milli>(mark);
    }

    // return a mark's cost , default in seconds
    long long CountSeconds(const std::string &mark = default_mark()) {
        return CountDuration<long long, std::chrono::seconds>(mark);
    }

    // return a mark's cost , default in minutes
    long CountMinutes(const std::string &mark = default_mark()) {
        return CountDuration<long, std::chrono::minutes>(mark);
    }

    // return a mark's cost , default in hours
    [[maybe_unused]] long CountHours(
        const std::string &mark = default_mark()) {
        return CountDuration<long, std::chrono::hours>(mark);
    }

    // return a mark's cost in nanoseconds
    std::string ShowTickNano(const std::string &mark = default_mark()) {
        return std::to_string(CountNano(mark)) + "ns";
    }

    // return a mark's cost in microseconds
    std::string ShowTickMicro(const std::string &mark = default_mark()) {
        return std::to_string(static_cast<double>(CountNano(mark)) / 1000.0) + "ws";
    }

    // return a mark's cost in milliseconds
    std::string ShowTickMills(const std::string &mark = default_mark()) {
        return std::to_string(static_cast<double>(CountMicro(mark)) / 1000.0) + "ms";
    }

    // return a mark's cost in seconds
    std::string ShowTickSeconds(const std::string &mark = default_mark()) {
        return std::to_string(static_cast<double>(CountMilli(mark)) / 1000.0) + "s";
    }

    // return a mark's cost in minutes
    std::string ShowTickMinutes(const std::string &mark = default_mark()) {
        return std::to_string(static_cast<double>(CountSeconds(mark)) / 60.0) + "m";
    }

    // return a mark's cost in hours
    std::string ShowTickHours(const std::string &mark = default_mark()) {
        return std::to_string(static_cast<double>(CountMinutes(mark)) / 60.0) + "h";
    }

    // remove mark and return its cost
    [[maybe_unused]] double Stop(
        const std::string &mark = default_mark()) {
        const auto cost = static_cast<double>(CountMilli(mark));
        mark_.erase(mark);
        return cost;
    }

    template<typename Fp>
    void Print(Fp fp, const std::string &mark = default_mark()) {
        if (print_on) {
            auto show = std::bind(fp, this, std::placeholders::_1);
            std::cout << mark << " takes " << show(mark) << std::endl;
        }
    }

    [[maybe_unused]] void PrintWithNano(
        const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickNano, mark);
    }

    [[maybe_unused]] void PrintWithMicro(
        const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickMicro, mark);
    }

    [[maybe_unused]] void PrintWithMills(
        const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickMills, mark);
    }

    [[maybe_unused]] void PrintWithSeconds(
        const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickSeconds, mark);
    }

    [[maybe_unused]] void PrintWithMinutes(
        const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickMinutes, mark);
    }

    [[maybe_unused]] void PrintWithHours(
        const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickHours, mark);
    }

protected:
    static std::string &default_mark() {
        static std::string inst("__default__");
        return inst;
    }

    // return a mark's cost
    template<typename Rep, typename Duration>
    Rep CountDuration(const std::string &mark = default_mark()) {
        return std::chrono::duration_cast<Duration>(
                    std::chrono::high_resolution_clock::now() - mark_[mark])
                .count();
    }

    // return a mark's cost
    template<typename Period>
    long long CountLL(const std::string &mark = default_mark()) {
        return CountDuration<long long, std::chrono::duration<long long, Period> >(
            mark);
    }

    std::unordered_map<std::string,
        std::chrono::high_resolution_clock::time_point>
    mark_;
    bool print_on;
};
