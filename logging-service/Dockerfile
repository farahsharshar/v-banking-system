FROM openjdk:11
WORKDIR /app
COPY target/logging-service.jar logging-service.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "logging-service.jar"]