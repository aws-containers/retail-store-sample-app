FROM maven:3.9.9-eclipse-temurin-21 AS build-env

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B -q

COPY ./src ./src

RUN ./mvnw -DskipTests package -q && \
    mv /workspace/target/ui-0.0.1-SNAPSHOT.jar /app.jar

FROM eclipse-temurin:21-jre-jammy

ENV APPUID=1000
ENV APPGID=1000
ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

COPY ./ATTRIBUTION.md ./LICENSES.md
COPY --chown=1000:1000 --from=build-env /app.jar .

EXPOSE 8080
USER 1000:1000

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]