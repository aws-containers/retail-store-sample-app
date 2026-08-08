# syntax=docker/dockerfile:1

# =========================================================
# Build stage
# Installs all dependencies because NestJS/TypeScript build
# tools are development dependencies.
# =========================================================
FROM node:20-alpine@sha256:fb4cd12c85ee03686f6af5362a0b0d56d50c58a04632e6c0fb8363f609372293 AS build

WORKDIR /app

# Copy dependency metadata first to improve Docker layer caching.
COPY package.json package-lock.json ./

# Deterministic installation using the committed lockfile.
RUN npm ci

# Copy application source only after dependencies are installed.
COPY . .

# Compile TypeScript/NestJS source into JavaScript under dist/.
RUN npm run build


# =========================================================
# Production dependencies stage
# Installs runtime dependencies only.
# =========================================================
FROM node:20-alpine@sha256:fb4cd12c85ee03686f6af5362a0b0d56d50c58a04632e6c0fb8363f609372293 AS production-dependencies

WORKDIR /app

COPY package.json package-lock.json ./

# Exclude development dependencies such as Nest CLI,
# TypeScript, Jest, ESLint, and build tooling.
RUN npm ci --omit=dev \
    && npm cache clean --force


# =========================================================
# Runtime stage
# Contains only compiled code and production dependencies.
# =========================================================
FROM node:20-alpine@sha256:fb4cd12c85ee03686f6af5362a0b0d56d50c58a04632e6c0fb8363f609372293 AS runtime

ARG BUILD_VERSION=dev
ARG BUILD_REVISION=unknown
ARG BUILD_DATE=unknown

ENV NODE_ENV=production

WORKDIR /app

LABEL org.opencontainers.image.title="retail-checkout" \
      org.opencontainers.image.description="Checkout service for the AWS Retail Store application" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${BUILD_REVISION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.source="https://github.com/ahmedmohsen5/retail-store-sample-app"

# Copy only runtime dependencies.
COPY --from=production-dependencies \
     --chown=node:node \
     /app/node_modules \
     ./node_modules

# Copy only compiled application output.
COPY --from=build \
     --chown=node:node \
     /app/dist \
     ./dist

COPY --chown=node:node package.json ./

# Avoid running the application as root.
USER node

HEALTHCHECK --interval=10s --timeout=5s --start-period=20s --retries=5 CMD node -e "fetch('http://127.0.0.1:8080/health').then(r => { if (!r.ok) process.exit(1) }).catch(() => process.exit(1))"

EXPOSE 8080

# CMD is used instead of ENTRYPOINT so Kubernetes or Docker
# can override the command temporarily during troubleshooting.
CMD ["node", "dist/main.js"]
