---
name: new-domain
description: 새 도메인 생성 종합 체크리스트. 완전히 새로운 도메인을 추가할 때 사용. $ARGUMENTS로 도메인명 전달.
disable-model-invocation: true
---
# New Domain Creation Checklist

도메인명: **$ARGUMENTS**

## 12단계 생성 순서

### Domain 모듈 (`domain/src/main/kotlin/org/balanceeat/domain/$ARGUMENTS/`)

1. **Entity.kt** — BaseEntity 상속, guard() 구현, 한국어 require 메시지
2. **{Entity}Result.kt** — data class, `companion object : EntityMapper<Entity, Result>` 구현
3. **{Entity}Command.kt** — `Create`, `Update` nested data class
4. **{Entity}Query.kt** — `Search` 조건 data class
5. **{Entity}Repository.kt** — `JpaRepository<Entity, Long>`
6. **{Entity}RepositoryCustom.kt + Impl** — QueryDSL 커스텀 쿼리
7. **{Entity}Reader.kt** — `@Component`, `BaseReader<Entity, Result>` 상속
8. **{Entity}Writer.kt** — `@Component`, `@Transactional`, create/update/delete

### Application 모듈 (`application/balance-eat-api/src/main/kotlin/org/balanceeat/api/$ARGUMENTS/`)

9. **{Entity}Service.kt** — `@Service`, Reader/Writer/Component 오케스트레이션
10. **{Domain}V1Payload.kt** — Request/Response nested data class
11. **{Domain}V1Controller.kt** — `@RestController`

### Test (`domain/src/testFixtures/`, `application/balance-eat-api/src/test/`)

12. **Fixtures + Tests**
    - `{Entity}Fixture.kt` — var 필드, 현실적 한국어 기본값
    - `{Entity}CommandFixture.kt` — nested class
    - `{Entity}V1PayloadFixture.kt` — Request/Response
    - 통합 테스트: `IntegrationTestContext` 상속
    - 컨트롤러 테스트: `ControllerTestContext` 상속

## 완료 확인
- `./gradlew :domain:test` 통과
- `./gradlew :application:balance-eat-api:test` 통과
- `make coverage` 80%+ 달성
