# Build Stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023 AS build-env

# We tell DNF not to install Recommends and Suggests packages, which are
# weak dependencies in DNF terminology, thus keeping our installed set of
# packages as minimal as possible.
RUN dnf --setopt=install_weak_deps=False install -q -y \
    git \
    golang \
    && \
    dnf clean all && \
    rm -rf /var/cache/dnf

RUN mkdir -p "${GOPATH}/src" "${GOPATH}/bin" /appsrc

WORKDIR /appsrc

# Use the official Go module mirror first; fall back to direct fetch.
ENV GOPROXY=https://proxy.golang.org,direct

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o main main.go

# Final stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023-minimal

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

WORKDIR /app

COPY --chown=1000:1000 --from=build-env /appsrc/main /app/

COPY ./ATTRIBUTION.md ./LICENSES.md

ENV GIN_MODE=release
EXPOSE 8080
USER 1000:1000

ENTRYPOINT ["/app/main"]
