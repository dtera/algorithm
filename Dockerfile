#FROM alpine:3.21
FROM ubuntu:24.04

LABEL org.opencontainers.image.source="https://github.com/dtera/algorithm"
LABEL name="algorithm"
LABEL version="1.0"
LABEL description="Docker image for building and running Java algorithm"
LABEL maintainer="dterazhao"
LABEL authors="dterazhao"

# update and install dependencies
RUN apk update && apk add --no-cache bash sudo

# set working directory
WORKDIR /workspace/algorithm
VOLUME /workspace/out

# maven compile the project
ADD . /workspace/algorithm
RUN cd /workspace/algorithm && rm -rf .vscode .idea .github .fleet .clwb bazel-* tmp  && mvn clean package
RUN cp /workspace/algorithm/native/target/native-*.jar /workspace/out/
