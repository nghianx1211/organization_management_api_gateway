# Stage 1: Build the Spring Boot application with Maven 3.8.4 and OpenJDK 17
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and the src directory
COPY pom.xml .
COPY src ./src

RUN mvn clean install
# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Run the Spring Boot application
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar

# Expose the port your Spring Boot app will run on
EXPOSE 8081

# Set the default environment variable for HOST_ENV
# ENV HOST_ENV=LOCAL

# Define the command to run the application with a conditional profile
# CMD sh -c 'if [ "$HOST_ENV" = "EC2" ]; then export SPRING_PROFILES_ACTIVE=dev; else export SPRING_PROFILES_ACTIVE=local; fi && java -jar -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE api-gateway.jar'
CMD ["java", "-jar", "api-gateway.jar"]