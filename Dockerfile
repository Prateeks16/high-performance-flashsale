# Use a lightweight Java 17 runtime
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
# (Make sure you run 'mvn clean package' first!)
COPY target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to start the app
ENTRYPOINT ["java", "-jar", "app.jar"]