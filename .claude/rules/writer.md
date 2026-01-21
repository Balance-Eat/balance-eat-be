---
paths:
  - "domain/src/main/**/*Writer.kt"
---
# Writer Rules

- **역할**: 도메인 쓰기의 **원자적 단위** — 하나의 쓰기 유스케이스를 한 트랜잭션으로 묶음
- `{Entity}Writer`: `@Component`, `@Transactional`
- Result 또는 도메인별 프로젝션 객체 반환. delete()는 Unit 반환 가능
- 메서드명: `create()`, `update()`, `delete()` 등 명확한 CUD 동사
- create/update 내 부가 로직(히스토리 로그 등) 포함 가능 — 같은 트랜잭션으로 원자성 보장
- 다른 도메인의 Writer 주입 금지 (도메인 경계 보호)
- 같은 도메인의 Reader 주입 가능
- Reader/Writer 간 순환 참조 금지
