# Balance-Eat Backend

## WHY: 프로젝트 개요
- 식단 관리 애플리케이션 백엔드
- Kotlin 2.0.20 + Spring Boot 3.4.4 + Java 21
- 멀티모듈 Gradle 프로젝트 (10개 서브모듈)

## WHAT: 아키텍처

### 모듈 구조
```
balance-eat-be/
├── common/                    # 공통 유틸, 기본 설정
├── domain/                    # 엔티티, Reader/Writer, Repository
├── supports/
│   ├── logging/              # 로깅
│   └── monitoring/           # 모니터링
├── client/
│   └── firebase/             # Firebase 클라이언트
└── application/
    ├── api-base/             # API 공통 (예외처리, 응답 래퍼)
    ├── balance-eat-api/      # 메인 API 서버
    └── balance-eat-admin-api/ # 어드민 API 서버
```

### 핵심 패턴
1. **Reader/Writer 패턴** — DomainService 대신. Reader(조회), Writer(CUD 원자적 단위)
2. **Result + EntityMapper 패턴** — DTO 대신. `companion object : EntityMapper<E, R>` 구현
3. **guard() 패턴** — 엔티티 유효성 검증. `fun` (kotlin-spring 플러그인에 의해 자동 open), @PrePersist/@PreUpdate 자동 호출
4. **Query 패턴** — 검색 조건 분리. Command(CUD), Query(조회)
5. **Service = 유스케이스 오케스트레이터** — 다양한 컴포넌트(Reader, Writer, Sender, Client 등) 조율. 자체 로직 최소화

### 계층 구조
```
Controller → AppService(유스케이스) → Reader/Writer/Component → Repository/Client
```

## HOW: 빌드 & 워크플로우

### 빌드 명령어
| 명령어 | 설명 |
|--------|------|
| `make test` | 전체 테스트 실행 |
| `make coverage` | Jacoco 커버리지 확인 (80%+) |
| `make lint` | ktlint 코드 스타일 검사 |
| `make lint-fix` | ktlint 자동 수정 |
| `make openapi` | OpenAPI3 스펙 생성 |
| `make build` | 테스트 + OpenAPI + Docker 빌드 |

### 개발 워크플로우
```
설계 → 구현 → 테스트 → 커버리지 80%+ → 코드리뷰 → 리팩토링 → 커밋
         ▲                                                    │
         └──────── 회귀 루프 (설계/구현 변경 필요 시) ◄────────┘
```

### 네이밍 규칙
| 파일 | 네이밍 |
|------|--------|
| 엔티티 | `{Entity}.kt` |
| 결과 | `{Entity}Result.kt` |
| 명령 | `{Entity}Command.kt` |
| 조회 | `{Entity}Query.kt` |
| 저장소 | `{Entity}Repository.kt` + `{Entity}RepositoryCustom.kt` + `Impl` |
| 조회기 | `{Entity}Reader.kt` |
| 기록기 | `{Entity}Writer.kt` |
| 서비스 | `{Entity}Service.kt` |
| 컨트롤러 | `{Domain}V1Controller.kt` |
| 페이로드 | `{Domain}V1Payload.kt` |
| 스케줄 서비스 | `{Domain}ScheduleService.kt` |
| 스케줄러 | `{Domain}Scheduler.kt` |
| 이벤트 | `{Entity}Event.kt` |
| 이벤트 리스너 | `{Entity}EventListener.kt` |
| 상세/요약 | `{Entity}Details.kt` / `{Entity}Summary.kt` |

### 주의사항 (Gotchas)
- 테이블명 예약어는 쌍따옴표 (`@Table(name = "\"user\"")`)
- `guard()`는 `fun` (kotlin-spring 플러그인에 의해 자동 open), 기본 Unit 반환
- 에러 메시지는 반드시 **한국어**
- TestFixture 위치: `src/testFixtures/kotlin`
- Result `companion object`는 `EntityMapper` 구현
- `NEW_ID = 0L` (새 엔티티 생성 시)

## Skills Reference
- `@.claude/skills/feature-dev/SKILL.md` — 기능 개발 7단계 워크플로우
- `@.claude/skills/new-domain/SKILL.md` — 새 도메인 생성 체크리스트
- `@.claude/skills/api-docs/SKILL.md` — OpenAPI 문서화
