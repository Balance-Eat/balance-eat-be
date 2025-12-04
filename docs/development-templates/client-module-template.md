# Balance-Eat Client Layer 표준 템플릿

## 필수 규칙

- **주석 금지**: 코드 내 주석은 작성하지 않습니다
- **예외 처리**: 외부 서비스 호출 시 적절한 ClientException을 발생시킵니다
- **설정 분리**: 외부 서비스 인증 정보는 application.yml로 관리합니다

## 목적

Balance-Eat 프로젝트의 Client 레이어 표준 템플릿을 제공합니다. 외부 서비스(Firebase, AWS 등)와의 통신을 담당하며, Kotlin + Spring Boot를 기반으로 합니다.

---

## 표준 패키지 구조

```
client/{service}/src/main/kotlin/org/balanceeat/client/{service}/
├── {Service}Client.kt              # 외부 서비스 클라이언트
├── {Service}Config.kt              # Spring Configuration
├── {Service}Payload.kt             # Request/Response 객체
└── {Service}ClientStatus.kt        # 에러 상태 정의

client/{service}/src/test/java/org/balanceeat/client/{service}/
├── Test{Service}Application.kt     # 테스트 애플리케이션
└── {Service}ClientTest.kt          # 클라이언트 테스트
```

**파일 설명**:
- **{Service}Client.kt**: 외부 서비스 API 호출 로직
- **{Service}Config.kt**: 외부 서비스 SDK 설정 및 Bean 등록
- **{Service}Payload.kt**: Request와 Response 객체 정의
- **{Service}ClientStatus.kt**: 외부 서비스 관련 에러 상태 정의
- **Test{Service}Application.kt**: 테스트용 Spring Boot 애플리케이션
- **{Service}ClientTest.kt**: 클라이언트 통합 테스트

---

## 모듈 의존성 구조

### build.gradle.kts

```kotlin
dependencies {
    implementation(project(":supports:jackson"))
    api(project(":client:base"))
    implementation("{external-service-sdk}")
}

tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}
```

**핵심 패턴**:
- **client:base**: 공통 예외 처리 및 인터페이스 제공
- **supports:jackson**: JSON 직렬화/역직렬화
- **bootJar disabled**: 라이브러리 모듈 (실행 가능한 JAR 아님)
- **jar enabled**: 일반 JAR로 패키징

---

## 템플릿 1: Client ({Service}Client.kt)

```kotlin
package org.balanceeat.client.firebase

import com.balanceeat.client.exception.ClientBadRequestException
import com.balanceeat.client.exception.ClientServerErrorException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FirebaseClient(
    private val firebaseMessaging: FirebaseMessaging
) {
    private val log = LoggerFactory.getLogger(FirebaseClient::class.java)

    fun sendMessage(request: FirebaseRequest.SendMessage) {
        val message = createMessage(request)

        try {
            firebaseMessaging.send(message)
        } catch (fme: FirebaseMessagingException) {
            if (fme.httpResponse.statusCode in 400..499) {
                log.warn("Firebase client error: ${fme.messagingErrorCode}, ${fme.errorCode}, ${fme.httpResponse.statusCode}, ${fme.message}", fme)
                throw ClientBadRequestException(FirebaseClientStatus.FIREBASE_ERROR, cause = fme)
            } else {
                log.error("Firebase server error: ${fme.messagingErrorCode}, ${fme.errorCode}, ${fme.httpResponse.statusCode}, ${fme.message}", fme)
                throw ClientServerErrorException(FirebaseClientStatus.FIREBASE_ERROR, cause = fme)
            }
        } catch (e: Exception) {
            log.error("Unexpected Firebase error: ${e.message}", e)
            throw ClientServerErrorException(FirebaseClientStatus.FIREBASE_ERROR, cause = e)
        }
    }

    private fun createMessage(request: FirebaseRequest.SendMessage): Message {
        val notification = Notification.builder()
            .setTitle(request.title)
            .setBody(request.content)
            .build()

        return Message.builder()
            .setNotification(notification)
            .setToken(request.deviceToken)
            .putData("deepLink", request.deepLink)
            .build()
    }
}
```

**핵심 패턴**:
- `@Component` 어노테이션으로 Spring Bean 등록
- 외부 SDK를 생성자 주입으로 의존성 관리
- `SLF4J Logger`로 에러 및 경고 로깅
- 예외 처리 계층화:
  - 4xx 에러 → `ClientBadRequestException` (warn 로그)
  - 5xx 에러 → `ClientServerErrorException` (error 로그)
  - 기타 예외 → `ClientServerErrorException` (error 로그)
- private 메서드로 요청 객체 생성 로직 분리
- 외부 서비스의 Response를 직접 반환하거나 변환하여 사용

---

## 템플릿 2: Config ({Service}Config.kt)

```kotlin
package org.balanceeat.client.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import java.net.URI

@Configuration
@Profile("!test")
class FirebaseConfig {
    @Value("\${application.firebase.project-id}")
    lateinit var projectId: String
    @Value("\${application.firebase.private-key-id}")
    lateinit var privateKeyId: String
    @Value("\${application.firebase.private-key}")
    lateinit var privateKey: String
    @Value("\${application.firebase.client-email}")
    lateinit var clientEmail: String
    @Value("\${application.firebase.client-id}")
    lateinit var clientId: String
    @Value("\${application.firebase.token-uri}")
    lateinit var tokenUri: String

    @Bean
    fun firebaseApp(googleCredentials: GoogleCredentials): FirebaseApp {
        val options = FirebaseOptions.builder()
            .setCredentials(googleCredentials)
            .build()

        return FirebaseApp.initializeApp(options)
    }

    @Bean
    @DependsOn("firebaseApp")
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }

    @Bean
    fun googleCredentials(): GoogleCredentials {
        return ServiceAccountCredentials.newBuilder()
            .setProjectId(projectId)
            .setPrivateKeyId(privateKeyId)
            .setPrivateKeyString(privateKey.replace("\\n", "\n"))
            .setClientEmail(clientEmail)
            .setClientId(clientId)
            .setTokenServerUri(URI.create(tokenUri))
            .build()
    }
}
```

**핵심 패턴**:
- `@Configuration` 어노테이션
- `@Profile("!test")`: 테스트 환경에서는 비활성화
- `@Value`로 application.yml에서 설정값 주입
- `@Bean`으로 외부 서비스 SDK 객체 등록
- `@DependsOn`으로 Bean 초기화 순서 제어
- 보안 정보는 환경 변수 또는 application.yml로 관리
- 외부 서비스 인증 정보 빌더 패턴 활용

**application.yml 예시**:
```yaml
application:
  firebase:
    project-id: ${FIREBASE_PROJECT_ID}
    private-key-id: ${FIREBASE_PRIVATE_KEY_ID}
    private-key: ${FIREBASE_PRIVATE_KEY}
    client-email: ${FIREBASE_CLIENT_EMAIL}
    client-id: ${FIREBASE_CLIENT_ID}
    token-uri: ${FIREBASE_TOKEN_URI:https://oauth2.googleapis.com/token}
```

---

## 템플릿 3: Payload ({Service}Payload.kt)

```kotlin
package org.balanceeat.client.firebase

class FirebaseRequest {
    data class SendMessage(
        val deviceToken: String,
        val title: String,
        val content: String,
        val deepLink: String
    )
}
```

**핵심 패턴**:
- Request와 Response를 별도 클래스로 분리
- Request 내부에 중첩 data class 정의
- 외부 서비스 API 스펙에 맞는 필드 정의
- Bean Validation은 Application 레이어에서 수행 (Client 레이어는 검증 불필요)

**Response 예시** (필요시):
```kotlin
class FirebaseResponse {
    data class SendResult(
        val messageId: String,
        val success: Boolean
    )
}
```

---

## 템플릿 4: Client Status ({Service}ClientStatus.kt)

```kotlin
package org.balanceeat.client.firebase

import org.balanceeat.common.Status

enum class FirebaseClientStatus(override val message: String): Status {
    FIREBASE_ERROR("파이어베이스 서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요."),
}
```

**핵심 패턴**:
- `Status` 인터페이스 구현
- 사용자에게 표시될 한글 메시지 작성
- 외부 서비스별 세분화된 에러 상태 정의 가능
- 예: `INVALID_TOKEN`, `QUOTA_EXCEEDED`, `SERVICE_UNAVAILABLE` 등

---

## 템플릿 5: Test Application (Test{Service}Application.kt)

```kotlin
package org.balanceeat.client.firebase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
@SpringBootApplication
class TestClientFirebaseApplication
```

**핵심 패턴**:
- `@TestConfiguration` + `@SpringBootApplication`
- 테스트 시 Spring Context 로드를 위한 애플리케이션
- 라이브러리 모듈에는 `@SpringBootApplication`이 없으므로 테스트용으로 제공

---

## 템플릿 6: Client Test ({Service}ClientTest.kt)

```kotlin
package org.balanceeat.client.firebase

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled
@SpringBootTest(classes = [TestClientFirebaseApplication::class])
@ActiveProfiles("test")
class FirebaseClientTest {
    @Autowired
    private lateinit var firebaseClient: FirebaseClient

    @Test
    fun manualTest() {
        val request = FirebaseRequest.SendMessage(
            title = "테스트 제목",
            content = "테스트 내용",
            deviceToken = "실제_디바이스_토큰",
            deepLink = "balanceeat://reminder"
        )

        firebaseClient.sendMessage(request)
    }
}
```

**핵심 패턴**:
- `@Disabled`: 수동 테스트 전용 (CI/CD에서 제외)
- `@SpringBootTest(classes = [TestClientFirebaseApplication::class])`: 테스트 애플리케이션 지정
- `@ActiveProfiles("test")`: 테스트 프로파일 활성화
- 실제 외부 서비스 호출이 필요한 경우 수동 테스트로 작성
- 자동화된 테스트는 Mock을 사용하거나 별도 작성

---

## Client Base 모듈 예외 처리

Client 모듈은 `client:base`에 정의된 공통 예외를 사용합니다.

### ClientException 계층

```kotlin
abstract class ClientException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null,
    val isCritical: Boolean = false
) : BaseException(status, message, cause)
```

**하위 예외**:
- `ClientBadRequestException`: 4xx 클라이언트 에러 (잘못된 요청, 인증 실패 등)
- `ClientUnauthorizedException`: 401 인증 에러
- `ClientNotFoundException`: 404 리소스 없음
- `ClientServerErrorException`: 5xx 서버 에러 (외부 서비스 장애 등)

**사용 예시**:
```kotlin
try {
    externalService.call()
} catch (e: ExternalServiceException) {
    when (e.statusCode) {
        in 400..499 -> throw ClientBadRequestException(ServiceStatus.ERROR, cause = e)
        in 500..599 -> throw ClientServerErrorException(ServiceStatus.ERROR, cause = e)
        else -> throw ClientServerErrorException(ServiceStatus.ERROR, cause = e)
    }
}
```

---

## 개발 체크리스트

### 새로운 Client 모듈 추가시

- [ ] **모듈 생성**: `client/{service}` 디렉토리 생성
- [ ] **build.gradle.kts**: 의존성 설정, bootJar/jar 설정
- [ ] **Client**: `{Service}Client.kt` 작성, `@Component` 적용
- [ ] **Config**: `{Service}Config.kt` 작성, Bean 등록
- [ ] **Payload**: `{Service}Payload.kt` 작성 (Request/Response)
- [ ] **Status**: `{Service}ClientStatus.kt` 작성, 에러 메시지 정의
- [ ] **Test Application**: `Test{Service}Application.kt` 작성
- [ ] **Test**: `{Service}ClientTest.kt` 작성 (수동 테스트)
- [ ] **application.yml**: 외부 서비스 설정 추가

### 코드 품질 체크

- [ ] 주석 없이 코드 작성
- [ ] 외부 서비스 SDK는 Config에서 Bean 등록
- [ ] 예외 처리 계층화 (4xx → ClientBadRequestException, 5xx → ClientServerErrorException)
- [ ] 보안 정보는 환경 변수 또는 application.yml로 관리
- [ ] 한글 에러 메시지 작성
- [ ] 테스트 환경에서 Config 비활성화 (`@Profile("!test")`)

---

## 명명 규칙 요약

| 항목 | 규칙 | 예시 |
|-----|------|------|
| 모듈 | `client/{service}` | `client/firebase` |
| Client | `{Service}Client.kt` | `FirebaseClient.kt` |
| Config | `{Service}Config.kt` | `FirebaseConfig.kt` |
| Payload | `{Service}Payload.kt` | `FirebasePayload.kt` |
| Status | `{Service}ClientStatus.kt` | `FirebaseClientStatus.kt` |
| Test Application | `Test{Service}Application.kt` | `TestClientFirebaseApplication.kt` |
| Test | `{Service}ClientTest.kt` | `FirebaseClientTest.kt` |

---

## 참고 사항

### Client 레이어 책임

**Client 레이어**:
- 외부 서비스 API 호출
- 외부 서비스 SDK 래핑
- Request 객체 → 외부 서비스 스펙 변환
- 외부 서비스 Response → 내부 객체 변환
- 외부 서비스 예외 → ClientException 변환
- 보안 정보 관리 (Config)

**하지 않는 것**:
- 비즈니스 로직 (Domain/Application 레이어 책임)
- Bean Validation (Application 레이어 책임)
- 트랜잭션 관리 (Application 레이어 책임)

### 외부 서비스 SDK 관리

**Config 클래스**:
- 외부 서비스 SDK의 인증 및 초기화 담당
- Bean으로 등록하여 Client에서 주입받아 사용
- 테스트 환경에서는 비활성화 (`@Profile("!test")`)

**보안 정보**:
- application.yml에 환경 변수 참조로 관리
- Git에는 절대 커밋하지 않음
- `.env` 파일 또는 시스템 환경 변수 사용

### 실제 Client 모듈 예시

**현재 프로젝트 구조**:
- **client/base**: 공통 예외 처리
- **client/firebase**: Firebase Cloud Messaging 연동
  - `FirebaseClient.kt`: 푸시 알림 전송
  - `FirebaseConfig.kt`: Firebase SDK 설정
  - `FirebasePayload.kt`: Request 객체
  - `FirebaseClientStatus.kt`: 에러 상태
  - `TestClientFirebaseApplication.kt`: 테스트 애플리케이션

---

이 템플릿을 따라 개발하면 일관성 있고 유지보수가 용이한 Client 레이어를 구축할 수 있습니다.
