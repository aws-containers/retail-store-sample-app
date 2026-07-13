FROM maven:3.9.16-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY .mvn .mvn/

COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src/ src/

ARG JAR_FILE=target/orders-0.0.1-SNAPSHOT.jar

RUN ./mvnw clean package -DskipTests -B \
    && test -f "${JAR_FILE}" \
    && cp "${JAR_FILE}" /app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache \
        curl \
        ca-certificates \
    && curl --version \
    && curl --version | grep -q "telnet"

ARG APP_UID=1000
ARG APP_GID=1000

RUN addgroup -S appgroup \
        -g "${APP_GID}" \
    && adduser -S appuser \
        -G appgroup \
        -h /app \
        -u "${APP_UID}"

WORKDIR /app

COPY --from=build \
    --chown=appuser:appgroup \
    /app.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS=""

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]