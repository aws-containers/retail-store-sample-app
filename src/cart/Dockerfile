FROM maven:3.9.16-eclipse-temurin-21-alpine AS build

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

FROM eclipse-temurin:21-jre-alpine AS runtime

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

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]