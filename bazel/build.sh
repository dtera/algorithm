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
    sudo apt update -y && apt install -y sudo wget gcc g++ cmake make m4 libomp-dev maven openjdk-21-jdk
    omp_so_path="$(find /usr -name 'libomp.so*' 2>/dev/null|head -1)"
    [ -f "$omp_so_path" ] && sudo ln -s "$omp_so_path" /usr/lib/libomp.so
    ldconfig
  elif [[ "$os_release" == "alpine" ]]; then
    echo "https://dl-cdn.alpinelinux.org/alpine/edge/testing" >> /etc/apk/repositories && apk update
    apk add linux-headers build-base cmake gcompat openmp-dev zlib-dev openssl-dev perl m4 bazel7 maven openjdk21
  else
    sudo yum install -y sudo wget gcc g++ cmake make libgomp maven java-21-openjdk
  fi

  if [[ "$os_release" != "alpine" ]]; then
    [ -f /usr/local/bin/bazelisk ] || sudo wget -O /usr/local/bin/bazelisk \
                                   https://github.com/bazelbuild/bazelisk/releases/download/v1.25.0/bazelisk-linux-amd64
    sudo chmod +x /usr/local/bin/bazelisk
    [ -f /usr/local/bin/bazel ] || sudo ln -s /usr/local/bin/bazelisk /usr/local/bin/bazel
  fi
else
  echo "not supported os type: ${OSTYPE}"
  exit 1
fi

cd ..
OPTS="-c opt --cxxopt=-DENABLE_IPCL=false"
# shellcheck disable=SC2086
bazel build $OPTS //native/src/main/native/cn/cstn/algorithm/javacpp/heu/... \
                  //native/src/main/native/cn/cstn/algorithm/javacpp:func
