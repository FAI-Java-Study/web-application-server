FROM alpine:3.20

# 기본 패키지 설치 및 open jdk8 다운로드 및 설치
RUN apk add --no-cache \
    curl \
    git \
    htop \
    tree \
    bash \
    wget \
    openjdk8 \
    && rm -rf /var/cache/apk/*

# 환경 변수 설정
ENV JAVA_HOME /opt/jdk
ENV PATH ${PATH}:${JAVA_HOME}/bin

WORKDIR /workspace

SHELL ["/bin/bash", "-c"]

CMD ["/bin/bash"]