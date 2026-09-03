# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY .mvn .mvn
COPY pom.xml .
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY ./src ./src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
LABEL org.opencontainers.image.source="https://github.com/yaboulhouda/retail-store-sample-app"
LABEL org.opencontainers.image.title="cart"
LABEL org.opencontainers.image.description="Cart service - retail store sample app"

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

RUN apk --no-cache add ca-certificates

RUN addgroup -g "$APPGID" "$APPUSER" && \
    adduser -D -u "$APPUID" -G "$APPUSER" -h /app "$APPUSER"

WORKDIR /app
USER $APPUSER
COPY --chown=$APPUSER:$APPUSER --from=build /build/target/carts-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT [ "java", "-jar", "app.jar" ]