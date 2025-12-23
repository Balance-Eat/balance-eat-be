# Dockerfile for Balance Eat API (Multi-stage build)

# 1단계: Build stage - Gradle로 애플리케이션 빌드
FROM eclipse-temurin:21-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 Gradle 설정 파일들 먼저 복사 (캐시 효율성을 위해)
COPY gradlew gradlew.bat ./
COPY gradle/ ./gradle/
COPY gradle.properties ./
COPY settings.gradle.kts ./
COPY build.gradle.kts ./

# 각 모듈의 build.gradle.kts 파일들 복사
COPY application/balance-eat-batch/build.gradle.kts ./application/balance-eat-batch/
COPY application/balance-eat-api/build.gradle.kts ./application/balance-eat-api/
COPY application/balance-eat-admin-api/build.gradle.kts ./application/balance-eat-admin-api/
COPY application/api-base/build.gradle.kts ./application/api-base/
COPY common/build.gradle.kts ./common/
COPY domain/build.gradle.kts ./domain/
COPY supports/jackson/build.gradle.kts ./supports/jackson/
COPY supports/monitoring/build.gradle.kts ./supports/monitoring/
COPY client/base/build.gradle.kts ./client/base/
COPY client/firebase/build.gradle.kts ./client/firebase/

# Gradle wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (소스 코드 변경 시 캐시 활용을 위해)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY . .

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew :application:balance-eat-api:bootJar -x test --no-daemon

# 2단계: Runtime stage - 실행 환경 구성
FROM eclipse-temurin:21-jre AS runtime

# 한국 시간대 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 애플리케이션을 실행할 사용자 생성 (보안을 위해)
RUN useradd -m -u 1001 appuser

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 런타임 스테이지로 복사
COPY --from=builder /app/application/balance-eat-api/build/libs/*.jar app.jar

# 애플리케이션 소유권을 appuser로 변경
RUN chown appuser:appuser app.jar

# appuser로 실행
USER appuser

# 애플리케이션 포트 (Spring Boot 기본 포트)
EXPOSE 8080

ARG DEBUG=false
ENV DEBUG=${DEBUG}

# 애플리케이션 실행
CMD ["sh", "-c", "if [ \"$DEBUG\" = \"true\" ]; then java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar app.jar; else java -jar app.jar; fi"]
