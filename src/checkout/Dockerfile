FROM node:20-alpine AS build
WORKDIR /usr/src/app
COPY --chown=node:node package*.json ./
COPY --chown=node:node . .
RUN yarn install --frozen-lockfile
RUN yarn build
# Prune devDependencies so that only what is required at runtime is copied
# into the final image.
RUN yarn workspaces focus --all --production
USER node


FROM public.ecr.aws/amazonlinux/amazonlinux:2023

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
    nodejs20 \
    shadow-utils \
    && \
    dnf clean all

RUN alternatives --install /usr/bin/node node /usr/bin/node-20 90

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

RUN useradd \
    --home "/app" \
    --create-home \
    --user-group \
    --uid "$APPUID" \
    "$APPUSER"

WORKDIR /app
USER appuser

COPY --chown=appuser:appuser --from=build /usr/src/app/node_modules ./node_modules
COPY --chown=appuser:appuser --from=build /usr/src/app/dist ./dist

ENTRYPOINT [ "node", "dist/main.js" ]
