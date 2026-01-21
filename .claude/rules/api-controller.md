---
paths:
  - "application/**/api/**/*.kt"
---
# API Controller Rules

## 구조
- `{Domain}V1ApiSpec.kt`: API 명세 인터페이스 (OpenAPI 어노테이션)
- `{Domain}V1Controller.kt`: `@RestController`, V1ApiSpec 구현
- `{Domain}V1Payload.kt`: Request/Response nested data class

## 테스트
- `@MockkBean(relaxed = true)` 사용

## RestDocs 헬퍼 문법
```kotlin
"field" type TYPE means "설명"
"field" type STRING means "설명" isOptional true
"field" type STRING means "설명" withEnum Enum::class
"id" pathMeans "설명"
"param" queryMeans "설명"
fieldsWithBasic(...)  // 기본 응답 필드 포함
```
