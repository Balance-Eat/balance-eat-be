# Simple working Dockerfile for Balance-Eat API
FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /app

# Copy gradle files
COPY gradlew gradlew.bat ./
COPY gradle/ ./gradle/
COPY gradle.properties settings.gradle.kts build.gradle.kts ./
COPY application/balance-eat-api/build.gradle.kts ./application/balance-eat-api/
COPY domain/build.gradle.kts ./domain/
COPY supports/jackson/build.gradle.kts ./supports/jackson/
COPY supports/monitoring/build.gradle.kts ./supports/monitoring/

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (without native services)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY . .

# Build application
RUN ./gradlew :application:balance-eat-api:bootJar -x test --no-daemon

# Production runtime
FROM eclipse-temurin:21-jre

# Create app user
RUN useradd -m appuser
WORKDIR /app
USER appuser

# Copy JAR
COPY --from=builder --chown=appuser:appuser /app/application/balance-eat-api/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "app.jar"]