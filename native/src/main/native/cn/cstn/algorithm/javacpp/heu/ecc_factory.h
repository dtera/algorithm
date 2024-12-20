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

    inline void register_ec_lib() {
        REGISTER_EC_LIBRARY(FourQ::kLibName, 1500, FourQ::IsSupported, FourQ::Create);
        REGISTER_EC_LIBRARY(sodium::kLibName, 800, sodium::IsSupported, sodium::Create);
        REGISTER_EC_LIBRARY(openssl::kLibName, 100, openssl::OpensslGroup::IsSupported, openssl::OpensslGroup::Create);
    }
} // namespace yacl::crypto
