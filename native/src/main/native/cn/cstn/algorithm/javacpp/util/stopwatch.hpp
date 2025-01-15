//
// Created by 2024 dterazhao.
//

#pragma once

#include <iostream>
#include <string>
#include <unordered_map>

enum TimeUnit: uint8_t {
    // Time unit representing one thousandth of a microsecond.
    NANOSECONDS = 0,
    // Time unit representing one thousandth of a millisecond.
    MICROSECONDS = 1,
    // Time unit representing one thousandth of a second.
    MILLISECONDS = 2,
    // Time unit representing one second.
    SECONDS = 3,
    // Time unit representing sixty seconds.
    MINUTES = 4,
    // Time unit representing sixty minutes.
    HOURS = 5,
    // Time unit representing twenty-four hours.
    DAYS = 6
};

inline std::string timeUnitToStr(const TimeUnit time_unit) {
    switch (time_unit) {
        case MICROSECONDS:
            return "microseconds";
        case MILLISECONDS:
            return "milliseconds";
        case SECONDS:
            return "seconds";
        case MINUTES:
            return "minutes";
        case HOURS:
            return "hours";
        case DAYS:
            return "days";
        default:
            return "nanoseconds";
    }
}

class StopWatch {
public:
    explicit StopWatch(const bool mark_default = false, const bool print_on = true) : total_duration(0),
        print_on(print_on), cur_mark_(default_mark()) {
        if (mark_default) {
            Mark();
        }
    }

    // start a new mark
    void Mark(const std::string &mark = default_mark()) {
        mark_[mark] = std::chrono::high_resolution_clock::now();
        cur_mark_ = mark;
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
    [[maybe_unused]] long CountHours(const std::string &mark = default_mark()) {
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
    [[maybe_unused]] void Stop(const std::string &mark = default_mark()) {
        const auto m = mark == default_mark() ? cur_mark_ : mark;
        const auto duration = CountNano(m);
        total_duration += duration;
        const auto d = std::find_if(task_durations_.begin(), task_durations_.end(),
                                    [&](const auto &t) { return t.first == m; });
        d == task_durations_.end()
            ? task_durations_.emplace_back(m, duration)
            : task_durations_[std::distance(task_durations_.begin(), d)] = std::make_pair(m, d->second + duration);
    }

    [[maybe_unused]] void PrettyPrint(const TimeUnit time_unit = MILLISECONDS) const {
        std::function stat_time = [](const double t) { return t; };
        switch (time_unit) {
            case MICROSECONDS:
                stat_time = [](const double t) { return t / 1e3; };
                break;
            case MILLISECONDS:
                stat_time = [](const double t) { return t / 1e6; };
                break;
            case SECONDS:
                stat_time = [](const double t) { return t / 1e9; };
                break;
            case MINUTES:
                stat_time = [](const double t) { return t / 1e9 / 60; };
                break;
            case HOURS:
                stat_time = [](const double t) { return t / 1e9 / 60 / 60; };
                break;
            case DAYS:
                stat_time = [](const double t) { return t / 1e9 / 60 / 60 / 24; };
                break;
            default:
                break;
        }
        printf("------------------------------------------------------\n");
        printf("%-14s %-5s         %-s\n", timeUnitToStr(time_unit).c_str(), "%", "mark");
        printf("------------------------------------------------------\n");
        for (const auto &[fst, snd]: task_durations_) {
            printf("%-6.8s       %-6.6s%%       %-s\n", std::to_string(stat_time(static_cast<double>(snd))).c_str(),
                   std::to_string(static_cast<double>(snd) / static_cast<double>(total_duration) * 100.0).c_str(),
                   fst.c_str());
        }
    }

    template<typename Fp>
    void Print(Fp fp, const std::string &mark = default_mark()) {
        if (print_on) {
            auto show = std::bind(fp, this, std::placeholders::_1);
            std::cout << mark << " takes " << show(mark) << std::endl;
        }
    }

    [[maybe_unused]] void PrintWithNano(const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickNano, mark);
    }

    [[maybe_unused]] void PrintWithMicro(const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickMicro, mark);
    }

    [[maybe_unused]] void PrintWithMills(const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickMills, mark);
    }

    [[maybe_unused]] void PrintWithSeconds(const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickSeconds, mark);
    }

    [[maybe_unused]] void PrintWithMinutes(const std::string &mark = default_mark()) {
        Print(&StopWatch::ShowTickMinutes, mark);
    }

    [[maybe_unused]] void PrintWithHours(const std::string &mark = default_mark()) {
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

    std::unordered_map<std::string, std::chrono::high_resolution_clock::time_point> mark_;
    std::vector<std::pair<std::string, long long> > task_durations_;
    long long total_duration;
    bool print_on;
    std::string cur_mark_;
};
