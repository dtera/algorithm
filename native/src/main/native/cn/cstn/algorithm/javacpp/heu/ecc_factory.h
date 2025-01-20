// Copyright 2023 Ant Group Co., Ltd.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
#pragma once

#include <map>

#include "yacl/crypto/ecc/FourQlib/FourQ_group.h"
#include "yacl/crypto/ecc/libsodium/ed25519_group.h"
#include "yacl/crypto/ecc/libsodium/x25519_group.h"
#include "yacl/crypto/ecc/openssl/openssl_group.h"
#include "yacl/crypto/ecc/toy/montgomery.h"
#include "yacl/crypto/ecc/toy/weierstrass.h"

namespace yacl::crypto {
    namespace FourQ {
        const std::string kLibName = "FourQlib";

        inline std::unique_ptr<EcGroup> Create(const CurveMeta &meta) {
            YACL_ENFORCE(meta.LowerName() == "fourq", "curve {} not supported",
                         meta.name);
            return std::make_unique<FourQGroup>(meta);
        }

        inline bool IsSupported(const CurveMeta &meta) { return meta.LowerName() == "fourq"; }
    }

    namespace sodium {
        const std::string kLibName = "libsodium";

        inline std::map<CurveName, CurveParam> kPredefinedCurves = {
            {
                "ed25519",
                {
                    2_mp .Pow(255) - 19_mp, // p = 2^255 - 19
                    2_mp .Pow(252) + "0x14def9dea2f79cd65812631a5cf5d3ed"_mp, // n
                    "8"_mp // h
                }
            },
            {
                "curve25519",
                {
                    2_mp .Pow(255) - 19_mp, // p = 2^255 - 19
                    2_mp .Pow(252) + "0x14def9dea2f79cd65812631a5cf5d3ed"_mp, // n
                    "8"_mp // h
                }
            }
        };

        inline std::unique_ptr<EcGroup> Create(const CurveMeta &meta) {
            YACL_ENFORCE(kPredefinedCurves.count(meta.LowerName()) > 0,
                         "curve {} not supported", meta.name);
            auto conf = kPredefinedCurves.at(meta.LowerName());

            if (meta.LowerName() == "ed25519") {
                return std::make_unique<Ed25519Group>(meta, conf);
            }
            if (meta.LowerName() == "curve25519") {
                return std::make_unique<X25519Group>(meta, conf);
            }
            YACL_THROW("unexpected curve {}", meta.name);
        }

        inline bool IsSupported(const CurveMeta &meta) {
            return kPredefinedCurves.count(meta.LowerName()) > 0;
        }
    }

    namespace openssl {
        static const std::string kLibName = "OpenSSL";
    }

    namespace toy {
        static std::map<CurveName, CurveParam> kPredefinedCurves = {
            {
                "secp256k1",
                {
                    "0x0"_mp, // A
                    "0x7"_mp, // B
                    {
                        "0x79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798"_mp,
                        "0x483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8"_mp
                    },
                    "0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f"_mp,
                    "0xfffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141"_mp,
                    "0x1"_mp // h
                }
            },
            // https://www.oscca.gov.cn/sca/xxgk/2010-12/17/content_1002386.shtml
            {
                "sm2",
                {
                    "0xFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC"_mp,
                    "0x28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93"_mp,
                    {
                        "0x32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7"_mp,
                        "0xBC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"_mp
                    },
                    "0xFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF"_mp,
                    "0xFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123"_mp,
                    "0x1"_mp // h
                }
            },
            {
                "curve25519",
                {
                    "486662"_mp, // A
                    "1"_mp, // B
                    {
                        "9"_mp,
                        "0x20ae19a1b8a086b4e01edd2c7748d14c923d4d7e6d7c61b229e9c5a27eced3d9"_mp
                    },
                    2_mp .Pow(255) - 19_mp, // p = 2^255 - 19
                    2_mp .Pow(252) + "0x14def9dea2f79cd65812631a5cf5d3ed"_mp, // n
                    "8"_mp // h
                }
            }
        };

        inline std::unique_ptr<EcGroup> Create(const CurveMeta &meta) {
            YACL_ENFORCE(kPredefinedCurves.count(meta.LowerName()) > 0,
                         "curve {} not supported", meta.name);
            auto conf = kPredefinedCurves.at(meta.LowerName());
            if (meta.form == CurveForm::Montgomery) {
                return std::make_unique<ToyXGroup>(meta, conf);
            }
            return std::make_unique<ToyWeierstrassGroup>(meta, conf);
        }

        inline bool IsSupported(const CurveMeta &meta) {
            return kPredefinedCurves.count(meta.LowerName()) > 0;
        }
    }

    inline void register_ec_lib() {
        REGISTER_EC_LIBRARY(FourQ::kLibName, 1500, FourQ::IsSupported, FourQ::Create);
        REGISTER_EC_LIBRARY(sodium::kLibName, 800, sodium::IsSupported, sodium::Create);
        REGISTER_EC_LIBRARY(openssl::kLibName, 100, openssl::OpensslGroup::IsSupported, openssl::OpensslGroup::Create);
        REGISTER_EC_LIBRARY(toy::kLibName, 10, toy::IsSupported, toy::Create);
    }
} // namespace yacl::crypto
