---
paths:
  - "application/**/src/main/**/*Service.kt"
---
# Service Rules

- **역할**: 클린 아키텍처의 **유스케이스(Use Case) 계층** — 일종의 main문
- `@Service` 어노테이션 사용
- **오케스트레이터 패턴**: 다양한 컴포넌트(Reader, Writer, Sender, Client 등)를 주입받아 조율
- **자체 비즈니스 로직 최소화**: 로직은 도메인 엔티티나 전용 컴포넌트에 위임
- Request → Command 변환 담당
- 권한 검증 담당 (인증된 사용자 확인)
- 메서드별 `@Transactional` / `@Transactional(readOnly = true)` 선언
- 다른 도메인의 Service 직접 주입 금지 → 해당 도메인의 Reader/Writer/Component 사용
