//
// Created by HqZhao on 2025/1/16.
//

#pragma once

class HeBuffer {
    std::string *data_;
    size_t size_;

public:
    ~HeBuffer() {
        delete[] data_;
    }

    explicit HeBuffer(const size_t size) : size_(size) {
        data_ = new std::string[size];
    }

    std::string &operator[](const std::size_t i) const {
        return data_[i];
    }

    void set(const std::size_t i, const std::string &data) const {
        data_[i] = data;
    }

    [[nodiscard]] size_t size() const {
        return size_;
    }
};
