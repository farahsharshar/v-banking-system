FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the jar file
COPY target/logging-service-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]