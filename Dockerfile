# ── Dockerfile ──
FROM eclipse-temurin:17-jdk

# JAR 빌드 결과물을 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 컨테이너 시작 시 이 명령어 실행
ENTRYPOINT ["java","-jar","/app.jar"]
