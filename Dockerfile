FROM openjdk:17-jdk-slim
COPY build/libs/scheduler-0.0.1-SNAPSHOT.jar scheduler.jar
ENTRYPOINT ["java", "-jar", "/scheduler.jar"]
