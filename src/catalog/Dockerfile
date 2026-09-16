# Build Stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023 AS build-env

# Apply all available security updates before installing packages. Amazon Linux
# 2023 locks DNF to the release snapshot the base image was built from, so
# --releasever=latest is required to pick up newer patched packages. In this
# stage it also ensures the Go toolchain (and the stdlib compiled into the
# binary) is the latest patched version.
# https://docs.aws.amazon.com/linux/al2023/ug/deterministic-upgrades-usage.html
#
# We tell DNF not to install Recommends and Suggests packages, which are
# weak dependencies in DNF terminology, thus keeping our installed set of
# packages as minimal as possible.
RUN dnf --releasever=latest upgrade -q -y \
    && \
    dnf --setopt=install_weak_deps=False install -q -y \
    git \
    golang \
    && \
    dnf clean all

RUN mkdir -p "${GOPATH}/src" "${GOPATH}/bin" /appsrc

WORKDIR /appsrc

ENV GOPROXY=https://proxy.golang.org,direct

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o main main.go

# Final stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

# Apply all available security updates before installing packages. Amazon Linux
# 2023 locks DNF to the release snapshot the base image was built from, so
# --releasever=latest is required to pick up newer patched packages.
# https://docs.aws.amazon.com/linux/al2023/ug/deterministic-upgrades-usage.html
#
# We tell DNF not to install Recommends and Suggests packages, which are
# weak dependencies in DNF terminology, thus keeping our installed set of
# packages as minimal as possible.
RUN dnf --releasever=latest upgrade -q -y \
    && \
    dnf --setopt=install_weak_deps=False install -q -y \
    shadow-utils \
    && \
    dnf clean all

RUN useradd \
    --home "/app" \
    --create-home \
    --user-group \
    --uid "$APPUID" \
    "$APPUSER"

WORKDIR /app
USER appuser

COPY --chown=appuser:appuser --from=build-env /appsrc/main /app/

COPY ./ATTRIBUTION.md ./LICENSES.md

ENV GIN_MODE=release

ENTRYPOINT ["/app/main"]
