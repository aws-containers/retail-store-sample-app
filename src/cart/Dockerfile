# src/cart/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline -B --no-transfer-progress

COPY src ./src
RUN ./mvnw clean package -DskipTests --no-transfer-progress

# Final stage
FROM eclipse-temurin:21-jre-alpine

RUN apk update && apk add --no-cache wget && adduser -D -u 1000 appuser

WORKDIR /app

COPY --from=builder --chown=1000:1000 /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS=""

USER 1000

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]