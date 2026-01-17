---
paths:
  - "domain/src/main/**/*Reader.kt"
---
# Reader Rules

- `{Entity}Reader`: `@Component`, `@Transactional(readOnly = true)`
- `BaseReader<E, R>` 상속 필수
- BaseReader 공통 메서드: `get()`, `getAll()`, `findById()` 등 — 불필요한 재정의 금지
- 항상 Result 타입 반환 (Entity 직접 노출 금지)
- 다른 도메인의 Reader 주입 가능 (조회는 도메인 경계 허용)
- Writer를 주입하지 않음 (단방향 의존)
