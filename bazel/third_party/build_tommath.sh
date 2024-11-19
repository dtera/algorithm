#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
. "$CD"/versions.sh
if [[ "$OSTYPE" == "darwin"* ]]; then
  # shellcheck disable=SC2155
  export SDKROOT=$(xcrun --show-sdk-path)
fi

# build libtommath
# [ -d libtommath ] || git clone https://github.com/libtom/libtommath.git
# shellcheck disable=SC2154
pkg="libtommath-$libtommath_ver"
download_url=https://github.com/libtom/libtommath/archive/"$libtommath_ver".tar.gz
sh "$CD"/build_template.sh --pkg "$pkg" -u "$download_url" -o \
"-DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_STANDARD=17 -DCMAKE_POSITION_INDEPENDENT_CODE=TRUE -DBUILD_SHARED_LIBS=ON -DCMAKE_INSTALL_LIBDIR=lib -DCMAKE_INSTALL_PREFIX=$CD" \
-d false

cp "$pkg"/tommath*.h include/libtommath/
rm -rf "$pkg"
