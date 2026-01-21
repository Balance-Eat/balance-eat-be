# Domain Module Conventions

## Reader/Writer 패턴
- `{Entity}Reader`: `@Component`, `@Transactional(readOnly = true)`, `BaseReader<E, R>` 상속
- `{Entity}Writer`: `@Component`, `@Transactional` — 도메인 쓰기의 원자적 단위
- Reader는 Result 반환, Writer는 Result 또는 도메인별 프로젝션 객체 반환. delete()는 Unit 반환 가능
- `BaseReader<E, R>` 공통 메서드: `existsById()`, `findById()`, `findByIdOrThrow()`, `findAllByIds()`, `count()`
- Writer 메서드명: `create()`, `update()`, `delete()` 등 명확한 CUD 동사
- Writer 내 부가 로직(히스토리 로그 등) 포함 가능 — 같은 트랜잭션 원자성 보장
- 다른 도메인의 Reader 주입 가능, Writer 주입 금지 (도메인 경계 보호)
- Reader/Writer 간 순환 참조 금지

## Result + EntityMapper 패턴
- `{Entity}Result`: data class, 엔티티의 외부 노출용 필드 포함
- `companion object : EntityMapper<Entity, EntityResult>` 구현 필수
- `from(entity: Entity): EntityResult` 변환 메서드
- 도메인 엔티티는 외부 직접 노출 금지 — 반드시 Result로 변환

## Entity + guard() 패턴
- `BaseEntity` 상속 필수
- `guard()`: `fun` (kotlin-spring 플러그인에 의해 자동 open, 기본 Unit, override 가능)
- `@PrePersist`/`@PreUpdate`에서 자동 호출
- `require()`로 검증, **한국어 에러 메시지**
- `NEW_ID = 0L` 상수 사용
- `@Enumerated(EnumType.STRING)`
- ID: `val`, 비즈니스 필드: `var`
- 선택적 필드: nullable + 기본값 `null`

## Command 패턴
- `{Entity}Command.kt`: CUD 전용 입력 객체
- nested data class: `Create`, `Update` 등
- 생성/수정에 필요한 필드만 포함

## Query 패턴
- `{Entity}Query.kt`: 검색/조회 조건 전용
- Command는 CUD, Query는 조회 — 명확히 분리
- QueryDSL `RepositoryCustom` + `Impl` 패턴과 함께 사용

## 패키지 구조
```
org.balanceeat.domain.{domain}/
├── {Entity}.kt
├── {Entity}Result.kt
├── {Entity}Command.kt
├── {Entity}Query.kt
├── {Entity}Repository.kt
├── {Entity}RepositoryCustom.kt + Impl
├── {Entity}Reader.kt
└── {Entity}Writer.kt
```

## Event 패턴
- `{Entity}Event.kt`: 도메인 이벤트 정의 (data class, sealed class)
- `{Entity}EventListener.kt`: `@Component`, `@TransactionalEventListener`
- Writer에서 `ApplicationEventPublisher`로 이벤트 발행
- EventListener에서 cross-domain 부수효과 처리

## 프로젝션 패턴
- `{Entity}Details`: 연관 엔티티 포함 상세 결과
- `{Entity}Summary`: 목록 조회용 요약 결과
- `EntityMapper` 미구현 — companion object의 `from()`/`of()` 정적 팩토리 메서드로 생성
