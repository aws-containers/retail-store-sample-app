# Build Stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023 as build-env

# We tell DNF not to install Recommends and Suggests packages, which are
# weak dependencies in DNF terminology, thus keeping our installed set of
# packages as minimal as possible.
RUN dnf --setopt=install_weak_deps=False install -q -y \
    git \
    golang \
    && \
    dnf clean all

ARG MAIN_PATH

RUN mkdir -p "${GOPATH}/src" "${GOPATH}/bin" /appsrc

WORKDIR /appsrc

COPY . .

ENV GOPROXY=direct
RUN go build -o main $MAIN_PATH

# Final stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

# We tell DNF not to install Recommends and Suggests packages, which are
# weak dependencies in DNF terminology, thus keeping our installed set of
# packages as minimal as possible.
RUN dnf --setopt=install_weak_deps=False install -q -y \
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
COPY --chown=appuser:appuser db/ /app/db/

COPY ./ATTRIBUTION.md ./LICENSES.md

ENV GIN_MODE=release

ENTRYPOINT ["/app/main"]
