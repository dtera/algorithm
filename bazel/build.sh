#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
echo "Current Directory: $CD"

if [[ "$OSTYPE" == "darwin"* ]]; then
  # shellcheck disable=SC2155
  export SDKROOT=$(xcrun --show-sdk-path)
fi

cd ..
OPTS="-c opt --cxxopt=-DENABLE_IPCL=false"
# shellcheck disable=SC2086
bazel query 'attr(testonly, 0, //native/src/main/native/cn/cstn/algorithm/javacpp/...)' | xargs bazel build $OPTS
