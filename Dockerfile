# --- Stage 1: Build the application with Maven and Java 17 ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime image without OTEL agent ---
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port
EXPOSE 8087

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
