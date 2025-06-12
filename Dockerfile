# ── Dockerfile ──
FROM eclipse-temurin:17-jdk

# JAR 빌드 결과물을 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 2) JVM 기본 옵션 설정
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

# 3) 컨테이너 시작 시 JAVA_OPTS 적용
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app.jar"]