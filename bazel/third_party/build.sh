#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
echo "Current Directory: $CD"

[ "$1" == "rm" ] && rm -rf include lib share
[ -d src ] || mkdir src
[ -d include ] || mkdir include
[ -d lib ] || mkdir lib
# build gmp
# PREFIX=$([[ "$1" == "" || "$1" == "rm" ]] && echo "$CD" || echo "$1")
# "$CD"/build_gmp.sh "$PREFIX"
# build tommath
"$CD"/build_tommath.sh

# shellcheck disable=SC2115
rm -rf "$CD"/lib/{cmake,pkgconfig,engines-1.1} "$CD"/{share,lib64}