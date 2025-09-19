FROM gradle:7.6-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app
VOLUME /tmp
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]