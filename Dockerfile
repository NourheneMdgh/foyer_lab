# --- Stage 1: Build the application with Maven and Java 17 ---
# Use an official Maven image that includes OpenJDK 17. This handles the installation for you.
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml first to leverage Docker's layer caching.
# Dependencies are only re-downloaded if pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Package the application, skipping the tests. Tests should be run in a separate CI/CD step.
RUN mvn package -DskipTests

# --- Stage 2: Create the final, lightweight runtime image ---
# Use an official OpenJDK 17 runtime image. It's much smaller than the full JDK.
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Expose the port your application will run on
EXPOSE 8087

# Copy the built JAR from the 'builder' stage to the final image
# Note: Adjust the JAR name if your pom.xml produces a different name.
COPY --from=builder /app/target/foyer-1.0.jar app.jar

# Command to run the application
CMD ["java","-jar","/app/app.jar"]