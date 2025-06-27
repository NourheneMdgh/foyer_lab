# --- Stage 1: Build the application with Maven and Java 17 ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime image with OTEL agent ---
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Use the 2.x autot-instrumentation agent (matches current GitHub)
ARG OTEL_AGENT_VERSION=2.17.0
RUN apk add --no-cache curl ca-certificates && \
    mkdir -p /otel && \
    curl -fSL \
      "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
      -o /otel/opentelemetry-javaagent.jar

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8087

CMD ["java",
     "-javaagent:/otel/opentelemetry-javaagent.jar",
     "-Dotel.exporter.otlp.endpoint=http://localhost:4317",
     "-Dotel.resource.attributes=service.name=tp-foyer",
     "-jar","/app/app.jar"]
