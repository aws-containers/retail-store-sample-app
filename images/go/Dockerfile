# Build Stage
FROM amazonlinux:2 as build-env

RUN yum install -q -y git golang && \
    yum clean all

ARG MAIN_PATH

RUN mkdir -p "${GOPATH}/src" "${GOPATH}/bin" /appsrc

WORKDIR /appsrc

COPY . .

RUN go build -o main $MAIN_PATH

# Final stage
FROM amazonlinux:2

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

RUN yum install -q -y \
    shadow-utils && \
    yum clean all

RUN useradd \
    --home "/app" \
    --create-home \
    --user-group \
    --uid "$APPUID" \
    "$APPUSER"

WORKDIR /app
USER appuser

COPY --chown=appuser:appuser --from=build-env /appsrc/main /app/
COPY --chown=appuser:appuser db/ /app/db/

COPY ./ATTRIBUTION.md ./LICENSES.md

ENV GIN_MODE=release

ENTRYPOINT ["/app/main"]