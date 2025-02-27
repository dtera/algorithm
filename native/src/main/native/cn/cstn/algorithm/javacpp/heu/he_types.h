//
// Created by HqZhao on 2025/2/25.
//

#pragma once

#include "heu/library/phe/phe.h"

typedef heu::lib::phe::SchemaType SchemaType;
typedef heu::lib::phe::Plaintext Plaintext;

class Ciphertext {
    int m_scale_cnt = 1;
    heu::lib::phe::Ciphertext *m_cipher;

public:
    Ciphertext(): m_cipher(new heu::lib::phe::Ciphertext()) {
    }

    explicit Ciphertext(heu::lib::phe::Ciphertext &cipher): m_cipher(new heu::lib::phe::Ciphertext()) {
        m_cipher = &cipher;
    }

    explicit Ciphertext(const heu::lib::phe::Ciphertext &cipher): m_cipher(new heu::lib::phe::Ciphertext()) {
        *m_cipher = cipher;
    }

    explicit Ciphertext(heu::lib::phe::Ciphertext *cipher) {
        m_cipher = cipher;
    }

    Ciphertext &operator=(heu::lib::phe::Ciphertext &cipher) {
        m_cipher = &cipher;
        return *this;
    }

    Ciphertext &operator=(const heu::lib::phe::Ciphertext &cipher) {
        *m_cipher = cipher;
        return *this;
    }

    Ciphertext &operator=(heu::lib::phe::Ciphertext *cipher) {
        m_cipher = cipher;
        return *this;
    }

    void operator ++() {
        m_scale_cnt++;
    }

    void operator --() {
        m_scale_cnt--;
    }

    void rescale(const int rescale_updater = 0) {
        if (rescale_updater != 0) { m_scale_cnt += rescale_updater; }
    }

    [[nodiscard]] int scale_cnt() const {
        return m_scale_cnt;
    }

    void set_scale_cnt(const int scale_cnt) {
        m_scale_cnt = scale_cnt;
    }

    [[nodiscard]] heu::lib::phe::Ciphertext *data() const {
        return m_cipher;
    }

    [[nodiscard]] heu::lib::phe::Ciphertext &c_data() const {
        return *m_cipher;
    }

    void copy_from(const Ciphertext &cipher) {
        *m_cipher = cipher.c_data();
        m_scale_cnt = cipher.scale_cnt();
    }

    void deserialize(const yacl::ByteContainerView &clazz) const {
        m_cipher->Deserialize(clazz);
    }
};

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
