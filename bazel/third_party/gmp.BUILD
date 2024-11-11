# Copyright 2024 dterazhao, Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("@rules_foreign_cc//foreign_cc:defs.bzl", "configure_make")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "all_srcs",
    srcs = glob(
        include = ["**"],
        exclude = ["*.bazel"],
    ),
)

configure_make(
    name = "gmp",
    args = ["-j 4"],
    configure_command = "configure",
    configure_in_place = True,
    configure_options = [ "--enable-fat" ],
    env = select({
        "@platforms//os:macos": {
            "AR": "",
        },
        "//conditions:default": {
            "MODULESDIR": "",
        },
    }),
    lib_name = "gmp",
    lib_source = ":all_srcs",
    linkopts = ["-ldl"],
    out_static_libs = ["libgmp.a"],
    targets = ["install"],
    visibility = ["//visibility:public"],
)