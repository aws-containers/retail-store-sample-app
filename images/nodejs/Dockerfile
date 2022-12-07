FROM amazonlinux:2

RUN yum install -q -y libstdc++.i686 glibc.i686 tar gzip xz && \
    yum clean all

ARG TARGETARCH
ENV NODE_VERSION 16.18.1

RUN DEBARCH="$TARGETARCH"; \
    if [ "$DEBARCH" = "amd64" ]; then DEBARCH=x64; fi; \
    echo "Pulling https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-${DEBARCH}.tar.xz" && \
    curl -LSs https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-${DEBARCH}.tar.xz -o node.tar.xz && \
    tar xf node.tar.xz --directory /tmp && \
    cp -R /tmp/node-v${NODE_VERSION}-linux-${DEBARCH}/* /usr && \
    rm -rf node-v${NODE_VERSION}-linux-${DEBARCH} node.tar.xz

WORKDIR /app

COPY package.json .
COPY package-lock.json .

RUN npm ci

ADD . /app
RUN npm run build

ENTRYPOINT [ "node", "dist/main.js" ]