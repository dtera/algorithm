#!/usr/bin/env bash

CD=$(cd "$(dirname "$0")" || exit && pwd)
cd "$CD" || exit
echo "Current Directory: $CD"

if [[ "$OSTYPE" == "darwin"* ]]; then
  # shellcheck disable=SC2155
  export SDKROOT=$(xcrun --show-sdk-path)
  brew install bazelisk libomp
elif echo "$OSTYPE" | grep -q "linux" || [[ "$OSTYPE" == "" ]]; then
  os_release=$(awk -F= '/^ID=/{print $2}' /etc/os-release)
  if [[ "$os_release" == "ubuntu" ]]; then
    sudo apt-get install -y npm
  else
    sudo yum install -y npm
  fi
  sudo npm install -g @bazel/bazelisk
else
  echo "not supported os type: ${OSTYPE}"
  exit 1
fi

cd ..
OPTS="-c opt --cxxopt=-DENABLE_IPCL=false"
# shellcheck disable=SC2086
bazel query 'attr(testonly, 0, //native/src/main/native/cn/cstn/algorithm/javacpp/...)' | xargs bazel build $OPTS
