---
paths:
  - "domain/src/main/**/domain/**/*.kt"
---
# Entity Rules

- `BaseEntity` 상속 필수
- `guard()`: `fun` (kotlin-spring 플러그인에 의해 자동 open, abstract 아님), 기본 Unit 반환
- `@PrePersist`/`@PreUpdate`에서 자동 호출
- `require()`로 검증, **한국어 에러 메시지**
- `@Enumerated(EnumType.STRING)`
- `NEW_ID = 0L` (새 엔티티 ID)
- 테이블명 예약어: 쌍따옴표 처리 (`@Table(name = "\"user\"")`)
- ID: `val`, 비즈니스 필드: `var`
- 선택적 필드: nullable + 기본값 `null`
