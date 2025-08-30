FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/account-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
