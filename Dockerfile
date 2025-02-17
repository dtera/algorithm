#FROM alpine:3.21
FROM ubuntu:24.04

LABEL org.opencontainers.image.source="https://github.com/dtera/algorithm"
LABEL name="algorithm"
LABEL version="1.0"
LABEL description="Docker image for building and running Java algorithm"
LABEL maintainer="dterazhao"
LABEL authors="dterazhao"

# update and install dependencies
RUN apt update -y && DEBIAN_FRONTEND=noninteractive apt install -y sudo bash maven openjdk-21-jdk

# create workspace directory
RUN mkdir -p /workspace/algorithm

# maven compile the project
ADD . /workspace/algorithm
RUN cd /workspace/algorithm && rm -rf .vscode .idea .github .fleet .clwb bazel-* tmp && mvn clean package

# set working directory
WORKDIR /workspace/out
RUN cp /workspace/algorithm/native/target/native-*-jar-with-dependencies.jar /workspace/out/
