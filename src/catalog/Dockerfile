FROM golang:1.23-alpine AS builder

ENV GOPROXY=https://goproxy.io,direct
ENV GO111MODULE=on
ENV CGO_ENABLED=0
ENV GOOS=linux

WORKDIR /app

# Copy go mod trước
COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -ldflags="-s -w" -o /catalog ./cmd/catalog

# Final stage
FROM alpine:3.20
RUN apk --no-cache add ca-certificates && adduser -D -u 1000 appuser

WORKDIR /app
COPY --from=builder /catalog /app/catalog

USER 1000
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["/app/catalog"]