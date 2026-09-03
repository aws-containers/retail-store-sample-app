
FROM golang:1.25-alpine AS build
RUN apk --no-cache add gcc musl-dev
ENV CGO_ENABLED=1
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o main .

FROM alpine:3.23
LABEL org.opencontainers.image.source="https://github.com/yaboulhouda/retail-store-sample-app"
LABEL org.opencontainers.image.title="catalog"
LABEL org.opencontainers.image.description="Catalog service - retail store sample app"

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

RUN apk --no-cache add ca-certificates

RUN addgroup -g "$APPGID" "$APPUSER" && \
    adduser -D -u "$APPUID" -G "$APPUSER" -h /app "$APPUSER"

WORKDIR /app
USER $APPUSER
COPY --chown=$APPUSER:$APPUSER --from=build /src/main /app/
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT [ "/app/main" ]
