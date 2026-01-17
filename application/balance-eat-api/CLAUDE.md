# Balance-Eat API Conventions

## Controller 테스트 패턴
- `@WebMvcTest` + `ControllerTestContext` 상속
- `@MockkBean(relaxed = true)`로 서비스 모킹
- 성공 케이스만 테스트 (`success()` 메서드)
- Nested 클래스로 API별 그룹화

## RestDocs 통합
- `restdocs-api-spec`으로 OpenAPI3 생성
- `Tags` enum: `tagName`, `descriptionWith("작업명")`
- `document()` + `ResourceSnippetParametersBuilder`

## 헬퍼 메서드 문법
```kotlin
"field" type TYPE means "설명"
"field" type STRING means "설명" isOptional true
"field" type STRING means "설명" withEnum Enum::class
"id" pathMeans "설명"
"param" queryMeans "설명"
fieldsWithBasic(...)  // 기본 응답 필드 포함
```

## Mock 원칙
- `every { service.method(any()) } returns mockData()`
- 해당 테스트에서 호출되는 메서드만 Mock 설정
- 불필요한 Mock 금지

## OpenAPI 생성
```bash
make openapi
# 또는
./gradlew :application:balance-eat-api:openapi3
```
