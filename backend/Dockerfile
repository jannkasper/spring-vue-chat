FROM maven:3.8-openjdk-17 AS build
WORKDIR /app

# Copy the POM and download dependencies (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the code and build the application
COPY src/ ./src/
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Create directory for file uploads
RUN mkdir -p /app/uploads

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 