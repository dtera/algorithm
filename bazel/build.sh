#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
echo "Current Directory: $CD"

# build third_party
"$CD"/third_party/build.sh

cd ..
bazel build -c opt //...
