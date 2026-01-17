---
paths:
  - "domain/src/main/**/*Command.kt"
---
# Command Rules

- `{Entity}Command.kt`: CUD 전용 입력 객체
- nested data class 패턴: `Create`, `Update` 등
- 생성/수정에 필요한 필드만 포함 (최소 원칙)
- 불변 객체: `val` 필드만 사용
- 검색 조건은 Command가 아닌 Query에 작성
