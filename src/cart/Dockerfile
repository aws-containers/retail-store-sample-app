FROM maven:3.9.16-eclipse-temurin-21-alpine@sha256:d88e5b38297858f65f97bc7e7964c760ab988fd18ace41589176f1468c49a489 AS build

WORKDIR /build



COPY .mvn .mvn/
COPY pom.xml .
COPY mvnw .

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src/ src/

RUN ./mvnw clean package -DskipTests -B \
    && cp target/*.jar /app.jar

FROM eclipse-temurin:21-jre-alpine@sha256:3f08b13888f595cc49edabea7250ba69499ba25602b267da591720769400e08c AS runtime

ARG BUILD_VERSION=dev
ARG BUILD_REVISION=unknown
ARG BUILD_DATE=unknown

LABEL org.opencontainers.image.title="retail-cart" \
      org.opencontainers.image.description="Cart service for the AWS Retail Store application" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${BUILD_REVISION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.source="https://github.com/ahmedmohsen5/retail-store-sample-app"

RUN addgroup -S appgroup \
    && adduser -S appuser -G appgroup -h /app \
    && apk add --no-cache \
        curl \
        ca-certificates

WORKDIR /app

COPY  --from=build \
    --chown=appuser:appgroup \
    /app.jar /app/app.jar

COPY --chown=appuser:appgroup \
    ATTRIBUTION.md ./

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=5 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]