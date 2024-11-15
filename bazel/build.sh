#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
echo "Current Directory: $CD"

# build third_party
#"$CD"/third_party/build.sh

cd ..
OPTS="-c opt"
#SED_F="native/src/main/native/cn/cstn/algorithm/javacpp/heu/BUILD.bazel"
#if [[ "$OSTYPE" == "darwin"* ]]; then
#  echo 6.5.0 > .bazelversion
#  sed -i "" 's@^#load("//bazel:cc_static_library.bzl"@load("//bazel:cc_static_library.bzl"@g' "$SED_F"
#else
#  echo 7.4.0 > .bazelversion
#  sed -i 's@^load("//bazel:cc_static_library.bzl"@#load("//bazel:cc_static_library.bzl"@g' "$SED_F"
#  OPTS="$OPTS --config=exp"
#fi
# --config=monolithic --spawn_strategy=standalone --genrule_strategy=standalone --output_filter=DONT_MATCH_ANYTHING
# shellcheck disable=SC2086
bazel build $OPTS //native/src/main/native/cn/cstn/algorithm/javacpp/...
