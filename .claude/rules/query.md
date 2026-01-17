---
paths:
  - "domain/src/main/**/*Query.kt"
---
# Query Rules

- `{Entity}Query.kt`: 검색/조회 조건 전용
- nested data class 패턴: `Search`, `Filter` 등
- Command와 분리: Command는 CUD, Query는 조회
- QueryDSL `RepositoryCustom` + `Impl` 패턴과 함께 사용
- 페이징 파라미터 포함 가능 (Pageable 연동)
