FROM golang:alpine@sha256:0178a641fbb4858c5f1b48e34bdaabe0350a330a1b1149aabd498d0699ff5fb2 AS build

RUN apk add --no-cache \
    git \
    ca-certificates

WORKDIR /build 

ENV GOPROXY=https://proxy.golang.org,direct

COPY go.mod go.sum ./

RUN go mod download \
    && go mod verify

COPY . .

RUN CGO_ENABLED=0 \
    GOOS=linux \
    go build \
    -trimpath \
    -ldflags="-s -w" \
    -o /out/main .
FROM alpine:latest@sha256:25109184c71bdad752c8312a8623239686a9a2071e8825f20acb8f2198c3f659 AS runtime

ARG BUILD_VERSION=dev
ARG BUILD_REVISION=unknown
ARG BUILD_DATE=unknown


RUN apk add --no-cache ca-certificates curl

ARG APP_UID=1000
ARG APP_GID=1000

RUN addgroup \
        -S \
        -g "${APP_GID}" \
        appgroup \
    && adduser \
        -S \
        -D \
        -H \
        -u "${APP_UID}" \
        -G appgroup \
        appuser
WORKDIR /app

COPY --from=build \
    --chown=appuser:appgroup \
    /out/main /app/main
LABEL org.opencontainers.image.title="retail-catalog" \
      org.opencontainers.image.description="Catalog service for the AWS Retail Store application" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${BUILD_REVISION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.source="https://github.com/ahmedmohsen5/retail-store-sample-app"

USER appuser

EXPOSE 8080


HEALTHCHECK --interval=10s --timeout=5s --start-period=20s --retries=5 \
    CMD curl -fsS http://127.0.0.1:8080/health || exit 1

ENTRYPOINT ["/app/main"]
