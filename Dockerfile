# --- Stage 1: Build the application with Maven and Java 17 ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy sources & build
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Install curl & CA certs, then download the OTEL Java agent
ARG OTEL_AGENT_VERSION=1.28.1
RUN apk add --no-cache curl ca-certificates \
 && mkdir -p /otel \
 && curl -fSL \
      "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
      -o /otel/opentelemetry-javaagent.jar

# Copy the Spring Boot JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8087

# Launch the app with the OTEL agent attached
CMD ["java",
     "-javaagent:/otel/opentelemetry-javaagent.jar",
     "-Dotel.exporter.otlp.endpoint=http://localhost:4317",
     "-Dotel.resource.attributes=service.name=tp-foyer",
     "-jar","/app/app.jar"]
