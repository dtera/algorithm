# Copyright 2024 dterazhao, Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
wrapper bazel cc_xx to modify flags.
"""
#load("@_builtins//:common/cc/experimental_cc_static_library.bzl", "cc_static_library")
load("//bazel:cc_static_library.bzl", "cc_static_library")
load("@yacl//bazel:yacl.bzl", "yacl_cc_library", "yacl_cc_binary", "yacl_cc_test",
                              "yacl_cmake_external", "yacl_configure_make")

load("@rules_cc//cc/private/toolchain:cc_configure.bzl", _cc_configure = "cc_configure")

INCLUDE_COPTS = ["-Inative/src/main/native/cn/cstn/algorithm/javacpp"] + ["-Ibazel/third_party/include"]

WARNING_FLAGS = [
    "-Wall",
    "-Wextra",
    "-Werror",
    "-Wno-unknown-pragmas",
]
# set `SPDLOG_ACTIVE_LEVEL=1(SPDLOG_LEVEL_DEBUG)` to enable debug level log
DEBUG_FLAGS = ["-DSPDLOG_ACTIVE_LEVEL=1", "-O0", "-g"]
RELEASE_FLAGS = ["-O2"]
FAST_FLAGS = ["-O1"]

def _algo_copts():
    return INCLUDE_COPTS + select({
        "@algo//bazel:algo_build_as_release": RELEASE_FLAGS,
        "@algo//bazel:algo_build_as_debug": DEBUG_FLAGS,
        "@algo//bazel:algo_build_as_fast": FAST_FLAGS,
        "//conditions:default": FAST_FLAGS,
    }) + WARNING_FLAGS

def add_prefix_for_local_deps(deps=[]):
    prefix_deps=[]
    if type(deps) == "list":
        for dep in deps:
            if not dep.startswith(("@", "//", ":")):
                dep = "//native/src/main/native/cn/cstn/algorithm/javacpp/" + dep
            prefix_deps.append(dep)
        return prefix_deps
    else:
        return deps

def algo_cc_binary(
        copts = [],
        deps = [],
        linkopts = [],
        **kargs):
    yacl_cc_binary(
        copts = _algo_copts() + copts,
        deps = add_prefix_for_local_deps(deps),
        linkopts = linkopts,
        **kargs
    )

def algo_cc_library(
        copts = [],
        deps = [],
        **kargs):
    yacl_cc_library(
        copts = _algo_copts() + copts,
        deps = add_prefix_for_local_deps(deps),
        **kargs
    )


def algo_cc_static_library(
        deps = [],
        **kargs):
    cc_static_library(
        deps = add_prefix_for_local_deps(deps),
        **kargs
    )

def algo_cc_test(
        copts = [],
        deps = [],
        linkopts = [],
        **kwargs):
    yacl_cc_test(
        copts = _algo_copts() + copts,
        deps = add_prefix_for_local_deps(deps),
        linkopts = linkopts,
        **kwargs
    )

def algo_cmake_external(**attrs):
    if "generate_args" not in attrs:
        attrs["generate_args"] = ["-GNinja"]
    return yacl_cmake_external(**attrs)

def algo_configure_make(**attrs):
    if "args" not in attrs:
        attrs["args"] = ["-j 8"]
    return yacl_configure_make(**attrs)
