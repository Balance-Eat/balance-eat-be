---
paths:
  - "application/**/api/**/*Payload.kt"
  - "application/**/*Payload.kt"
---
# Payload Rules

- `{Entity}V1Payload.kt`: API 계층의 Request/Response 객체
- nested data class 패턴: `Request`, `Response` 등
- Request → Command 변환은 Service에서 담당
- Result → Response 변환은 Payload 내 companion/확장 함수로
- validation 어노테이션 (`@NotBlank`, `@Size` 등)은 Request에 선언
- OpenAPI 어노테이션 (`@Schema`)은 V1ApiSpec 인터페이스에 선언
