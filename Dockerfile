FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/Project-Java-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
