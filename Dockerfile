FROM openjdk:17-jdk-slim

# 2. Set a working directory inside the container
WORKDIR /app

# 3. Copy the packaged Spring Boot application JAR file from your local 'target' directory
#    into the container's working directory.
#    Replace 'tp-foyer-5.0.0.jar' with the actual name of your JAR file
#    if it's different (e.g., if you have a custom finalName in your pom.xml).
COPY target/tp-foyer-5.0.0.jar app.jar

# 4. Expose the port your Spring Boot application listens on.
#    Default for Spring Boot is 8080. Change this if your application uses a different port.
EXPOSE 8082

# 5. Define the command to run when the container starts.
#    This runs your Spring Boot application.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]