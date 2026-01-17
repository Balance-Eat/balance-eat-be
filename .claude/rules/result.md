---
paths:
  - "domain/src/main/**/*Result.kt"
---
# Result Rules

- `{Entity}Result`: data class, 엔티티의 외부 노출용 필드 포함
- `companion object : EntityMapper<Entity, EntityResult>` 구현 필수
- `from(entity: Entity): EntityResult` 변환 메서드
- 도메인 엔티티를 외부에 직접 노출하지 않음 — 반드시 Result로 변환
- 연관 엔티티도 각각의 Result로 변환하여 포함
- DTO 대신 Result 용어 사용 (프로젝트 컨벤션)
