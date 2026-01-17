# Application Layer Conventions

## Application Service (유스케이스 계층)
- `{Entity}Service`: `@Service` 어노테이션
- 클린 아키텍처의 유스케이스 = 일종의 main문
- 다양한 컴포넌트(Reader, Writer, Sender, Client 등)를 주입받아 오케스트레이션
- 자체 비즈니스 로직 최소화 — 로직은 도메인 엔티티나 전용 컴포넌트에 위임
- Request → Command 변환 담당
- 권한 검증 담당
- `@Transactional` 선언하지 않음 (각 컴포넌트가 자체 관리)
- 다른 도메인의 Service 직접 주입 금지 → 해당 도메인의 Reader/Writer/Component 사용

## API 계층 구조
- `{Domain}V1ApiSpec.kt`: API 명세 인터페이스 (OpenAPI 어노테이션)
- `{Domain}V1Controller.kt`: `@RestController`, V1ApiSpec 구현
- `{Domain}V1Payload.kt`: Request/Response nested data class

## 표준 응답
- `ApiResponse<T>`: 표준 응답 래퍼
- 예외 처리: `ApplicationException`, `DomainException`, `DomainStatus` enum

## URL 규칙
- `/api/v1/{domain}` 경로 사용
- RESTful 설계 원칙 준수
