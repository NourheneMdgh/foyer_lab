# --- Stage 1: Build the application with Maven and Java 17 ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies (Docker layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the full source code
COPY src ./src

# Package the application (skip tests)
RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Expose the Spring Boot default port (or your actual one)
EXPOSE 8087

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the jar
CMD ["java", "-jar", "app.jar"]
