# src/ui/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven files trước để cache dependencies
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline -B --no-transfer-progress

# Copy source code
COPY src ./src

# Build
RUN ./mvnw clean package -DskipTests --no-transfer-progress

# Final stage
FROM eclipse-temurin:21-jre-alpine

RUN apk --no-cache add curl && adduser -D -u 1000 appuser

WORKDIR /app

# Copy JAR từ builder stage
COPY --from=builder /app/target/*.jar app.jar

USER 1000

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]