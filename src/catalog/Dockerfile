# src/catalog/Dockerfile
FROM golang:1.23-alpine AS builder

ENV GOPROXY=https://proxy.golang.org,direct
ENV GO111MODULE=on
ENV CGO_ENABLED=0
ENV GOOS=linux

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download -x

COPY . .

RUN go build -ldflags="-s -w" -o /app/catalog .

# Final stage
FROM alpine:3.20

RUN apk update && apk add --no-cache ca-certificates wget && adduser -D -u 1000 appuser

WORKDIR /app

COPY --from=builder --chown=1000:1000 /app/catalog ./catalog

USER 1000

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -qO- http://localhost:8080/health || exit 1

ENTRYPOINT ["./catalog"]