#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit

# shellcheck disable=SC2034
gmp_ver=6.3.0
# shellcheck disable=SC2034
libtommath_ver=42b3fb07e7d504f61a04c7fca12e996d76a25251
