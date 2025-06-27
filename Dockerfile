# --- Stage 1: Build the application with Maven and Java 17 ---

FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Set working directory inside the container

WORKDIR /app

# Copy pom.xml and download dependencies (Docker layer caching)

COPY pom.xml .
RUN mvn dependency\:go-offline

# Copy the full source code

COPY src ./src

# Package the application (skip tests)

RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---

FROM openjdk:17-jdk-alpine

# Set working directory

WORKDIR /app

# Download and install the OpenTelemetry Java agent

ARG OTEL\_AGENT\_VERSION=1.28.1
RUN apk add --no-cache wget&#x20;
&& mkdir -p /otel&#x20;
&& wget -qO /otel/opentelemetry-javaagent.jar&#x20;
[https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v\${OTEL\_AGENT\_VERSION}/opentelemetry-javaagent.jar](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar)

# Copy the Spring Boot jar from the builder stage

COPY --from=builder /app/target/\*.jar app.jar

# Expose the application port

EXPOSE 8087

# Launch the app with the OTEL agent attached

CMD \[
"java",
"-javaagent:/otel/opentelemetry-javaagent.jar",
"-Dotel.exporter.otlp.endpoint=http\://localhost:4317",
"-Dotel.resource.attributes=service.name=tp-foyer",
"-jar",
"/app/app.jar"
]
