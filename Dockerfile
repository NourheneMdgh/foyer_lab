# --- Stage 1: Build the application with Maven and Java 17 ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# cache deps
COPY pom.xml .
RUN mvn dependency:go-offline

# copy source & package
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---
FROM openjdk:17-jdk-alpine

# where we'll install the agent & app
WORKDIR /app

# pull in OTEL java-agent
ARG OTEL_AGENT_VERSION=1.28.1
RUN apk add --no-cache wget \
 && mkdir -p /otel \
 && wget -qO /otel/opentelemetry-javaagent.jar \
      https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar

# copy your Spring Boot jar
COPY --from=builder /app/target/*.jar app.jar

# expose your app port (and collector-sidecar metrics if you like)
EXPOSE 8087

# start with the OTEL agent attached
CMD ["java",
     "-javaagent:/otel/opentelemetry-javaagent.jar",
     "-Dotel.exporter.otlp.endpoint=http://localhost:4317",
     "-Dotel.resource.attributes=service.name=tp-foyer",
     "-jar","/app/app.jar"]
