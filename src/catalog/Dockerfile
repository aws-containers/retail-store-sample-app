FROM golang:alpine AS build

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
FROM alpine:latest AS runtime

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

USER appuser

EXPOSE 8080

ENTRYPOINT ["/app/main"]