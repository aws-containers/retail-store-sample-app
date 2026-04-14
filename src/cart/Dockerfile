FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY src/cart/pom.xml ./
RUN ./mvnw dependency:go-offline -B --no-transfer-progress
COPY src/cart/src ./src
RUN ./mvnw clean package -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine
RUN apk --no-cache add curl && adduser -D -u 1000 appuser
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER 1000
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]