#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
echo "Current Directory: $CD"

suffix="so"
if [[ "$OSTYPE" == "darwin"* ]]; then
  suffix="dylib"
fi

osname=$(uname|tr '[:upper:]' '[:lower:]')
if [[ "$osname" == "darwin"* ]]; then
  # shellcheck disable=SC2155
  export SDKROOT=$(xcrun --show-sdk-path)
  osname="macosx"
fi
arch=$(arch)
src_dir="$CD/cn/cstn/algorithm/javacpp"
out_dir="$src_dir/$osname-$arch"
[ -d "$out_dir" ] || mkdir -p "$out_dir"

g++ --std=c++17 -fPIC -shared "$src_dir/func.cpp" -o "${out_dir}/libfunc.${suffix}"

#g++ "$src_dir/test.cpp" -o "${out_dir}/test" "${out_dir}/libfunc.${suffix}"
#"${out_dir}/test"