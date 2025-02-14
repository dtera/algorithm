FROM alpine:3.21

LABEL org.opencontainers.image.source="https://github.com/dtera/algorithm"
LABEL name="algorithm"
LABEL version="1.0"
LABEL description="Docker image for building and running Java algorithm"
LABEL maintainer="dterazhao"
LABEL authors="dterazhao"

# set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$PATH:$JAVA_HOME/bin

# set working directory
WORKDIR /workspace/algorithm
VOLUME /workspace/out

# maven compile the project
ADD . /workspace/algorithm
RUN cd /workspace/algorithm && rm -rf .vscode .idea .github .fleet .clwb bazel-* tmp  && mvn clean package
RUN cp /workspace/algorithm/native/target/native-*.jar /workspace/out/
