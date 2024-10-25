#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit

# shellcheck disable=SC2034
gmp_ver=6.3.0
