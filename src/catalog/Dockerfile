FROM golang:1.23-alpine AS builder
WORKDIR /app
COPY src/catalog/go.mod src/catalog/go.sum ./
RUN go mod download
COPY src/catalog/ ./
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o /catalog ./cmd/catalog

FROM alpine:3.20
RUN apk --no-cache add ca-certificates && adduser -D -u 1000 appuser
COPY --from=builder /catalog /catalog
USER 1000
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1
ENTRYPOINT ["/catalog"]