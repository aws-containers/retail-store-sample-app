# syntax=docker/dockerfile:1

# =========================================================
# Build stage
# =========================================================
FROM maven:3.9.16-eclipse-temurin-21-alpine@sha256:d88e5b38297858f65f97bc7e7964c760ab988fd18ace41589176f1468c49a489 AS build

WORKDIR /build

# Copy Maven metadata first to improve dependency caching.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw \
    && ./mvnw dependency:go-offline -B

# Copy source only after dependencies are cached.
COPY src/ src/

ARG JAR_FILE=target/orders-0.0.1-SNAPSHOT.jar

RUN ./mvnw clean package -DskipTests -B \
    && test -f "${JAR_FILE}" \
    && mkdir -p /out \
    && cp "${JAR_FILE}" /out/app.jar


# =========================================================
# Runtime stage
# =========================================================
FROM eclipse-temurin:21-jre-alpine@sha256:3f08b13888f595cc49edabea7250ba69499ba25602b267da591720769400e08c AS runtime

ARG BUILD_VERSION=dev
ARG BUILD_REVISION=unknown
ARG BUILD_DATE=unknown

ARG APP_UID=1000
ARG APP_GID=1000

USER root

# curl is used by the image health check.
RUN apk add --no-cache \
        curl \
        ca-certificates \
    && addgroup -S appgroup -g "${APP_GID}" \
    && adduser -S appuser \
        -G appgroup \
        -h /app \
        -u "${APP_UID}"

WORKDIR /app

COPY --from=build \
    --chown=appuser:appgroup \
    /out/app.jar \
    /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS=""

LABEL org.opencontainers.image.title="retail-orders" \
      org.opencontainers.image.description="Orders service for the AWS Retail Store application" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${BUILD_REVISION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.source="https://github.com/ahmedmohsen5/retail-store-sample-app"

USER appuser

EXPOSE 8080

HEALTHCHECK \
    --interval=10s \
    --timeout=5s \
    --start-period=30s \
    --retries=5 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
