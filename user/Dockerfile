# 1단계: JAR 빌드
FROM gradle:8.4-jdk17 AS builder

WORKDIR /app

COPY ../gradlew ./gradlew
COPY ../gradle ./gradle
COPY ../build.gradle ../settings.gradle ./
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew :user:bootJar -x test --no-daemon
RUN ls -alR ./user

# 2단계: 런타임 환경
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/user/build/libs/*.jar app.jar

# 환경변수 적용 후 jar 실행
CMD ["java", "-jar", "app.jar"]
