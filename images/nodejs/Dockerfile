FROM amazonlinux:2

RUN yum install -q -y libstdc++.i686 glibc.i686 tar gzip xz shadow-utils && \
    yum clean all

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

RUN useradd \
    --home "/app" \
    --create-home \
    --user-group \
    --uid "$APPUID" \
    "$APPUSER"

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
USER appuser

COPY --chown=appuser:appuser package.json .
COPY --chown=appuser:appuser package-lock.json .

RUN npm ci

ADD . /app
RUN npm run build

ENTRYPOINT [ "node", "dist/main.js" ]