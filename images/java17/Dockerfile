# Build Stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023 as build-env

# We tell DNF not to install Recommends and Suggests packages, keeping our
# installed set of packages as minimal as possible.
RUN dnf --setopt=install_weak_deps=False install -q -y \
    maven \
    java-17-amazon-corretto-headless \
    which \
    && \
    dnf clean all

ARG JAR_PATH

VOLUME /tmp
WORKDIR /

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B -q

COPY ./src ./src

RUN ./mvnw -DskipTests package -q && \
    mv /$JAR_PATH /app.jar

# Package Stage
FROM public.ecr.aws/amazonlinux/amazonlinux:2023

# We tell DNF not to install Recommends and Suggests packages, which are
# weak dependencies in DNF terminology, thus keeping our installed set of
# packages as minimal as possible.
RUN dnf --setopt=install_weak_deps=False install -q -y \
    java-17-amazon-corretto-headless \
    shadow-utils \
    && \
    dnf clean all

ARG aws_opentelemetry_agent_version=1.24.0

ENV APPUSER=appuser
ENV APPUID=1000
ENV APPGID=1000

RUN useradd \
    --home "/app" \
    --create-home \
    --user-group \
    --uid "$APPUID" \
    "$APPUSER"

RUN curl -L https://github.com/aws-observability/aws-otel-java-instrumentation/releases/download/v${aws_opentelemetry_agent_version}/aws-opentelemetry-agent.jar -o /opt/aws-opentelemetry-agent.jar && \
    curl -L https://raw.githubusercontent.com/aws-observability/aws-otel-java-instrumentation/v${aws_opentelemetry_agent_version}/licenses/licenses.md -o aws-opentelemetry-agent-licenses.md
ENV JAVA_TOOL_OPTIONS=-javaagent:/opt/aws-opentelemetry-agent.jar
ENV OTEL_JAVAAGENT_ENABLED=false

WORKDIR /app
USER appuser

COPY ./ATTRIBUTION.md ./LICENSES.md
COPY --chown=appuser:appuser --from=build-env /app.jar .

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
