# 1단계: 빌드 단계
FROM gradle:8-jdk17 AS builder
WORKDIR /app

# Gradle 빌드 캐시 활용을 위해 먼저 build.gradle과 settings.gradle만 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle build -x test --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 실제 빌드
RUN gradle clean build -x test --no-daemon

# 2단계: 실행 단계
FROM amazoncorretto:17
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/scheduler-0.0.1-SNAPSHOT.jar /scheduler.jar

# Google Cloud credentials 복사
COPY scheduler-466515-c8c1c8ece296.json /app/credentials.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/credentials.json

# 실행
ENTRYPOINT ["java", "-jar", "/scheduler.jar"]
