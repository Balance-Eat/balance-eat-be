---
paths:
  - "**/test/**/*.kt"
  - "**/testFixtures/**/*.kt"
---
# Testing Rules

## Fixture 패턴
- `TestFixture<T>` 인터페이스 패턴: `create(): T`
- Entity Fixture: `{Entity}Fixture.kt` — var 필드, 기본값, `create()` 메서드
- Command Fixture: `{Entity}CommandFixture.kt` — nested class 패턴
- Payload Fixture: `{Entity}V1PayloadFixture.kt` — Request/Response 통합
- 위치: `src/testFixtures/kotlin/org/balanceeat/domain/{domain}/`
- 기본값: 현실적인 한국어 데이터
- 컬렉션 필드: `MutableList` 사용

## 검증 패턴
- `usingRecursiveComparison().ignoringFields(...)` 사용

## 테스트 컨텍스트
- 통합 테스트: `IntegrationTestContext` 상속 (Testcontainers PostgreSQL)
- 컨트롤러 테스트: `ControllerTestContext` 상속 (WebMvcTest)

## 테스트 메서드명
- 한국어 백틱: `` `사용자를 생성할 수 있다` ``
