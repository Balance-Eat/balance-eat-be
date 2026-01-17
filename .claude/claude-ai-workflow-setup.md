# Balance-Eat AI 워크플로우 전면 재구성 계획 v2.1

> **v2 → v2.1 변경사항**:
> - 단계 추적 프로토콜 추가 (TaskCreate로 7단계 진행 추적)
> - Hook 강화: 테스트→커버리지 자동 확인, PreCommit lint 자동 검증
> - Jacoco exclude 패턴 추가 (data class, config 등 제외)
> - Agent frontmatter에서 비공식 `tools:`/`skills:` 필드 제거
> - Phase 2→3 순차화 (CLAUDE.md 참조 의존성 반영)
> - `/api-docs` Skill 자동 호출 허용
> - settings.json ↔ settings.local.json 병합 관계 명시
> - 워크플로우 회귀 루프 반영 (리팩토링/코드리뷰 → 설계/구현 회귀 가능)
> - Rules 3개→10개 확장 (reader, writer, service, command, result, query, payload 추가)

## Context

### 원래 요청
기존 `claude/` 디렉토리와 `CLAUDE.md`를 삭제하고, 2026년 최신 Claude Code 베스트 프랙티스에 맞춘 AI 워크플로우를 처음부터 구축한다.

### 유저 워크플로우
```
설계 → 구현 → 테스트코드 작성 → Jacoco 커버리지 80%+ → 코드리뷰 (OMC agent) → 리팩토링 → 커밋
```

### 조사 결과 요약
- 기존 `claude/` 가이드라인은 **구식 패턴**(DomainService, DTO)을 기술하고 있음
- 실제 코드베이스는 이미 **Reader/Writer 패턴**, **Result 패턴**, **Query 패턴**으로 진화함
- `.claude/settings.json` 미존재, Skills/Hooks/Agents 전무
- **Jacoco 미설정**, **ktlint 주석처리** 상태
- 2026 최신 베스트 프랙티스: 계층적 CLAUDE.md + `.claude/rules/` + Skills + Agents

### 핵심 발견 사항
| 가이드라인 기술 내용 | 실제 코드베이스 |
|---|---|
| `{Entity}DomainService` | `{Entity}Reader` + `{Entity}Writer` |
| `{Entity}Dto` 반환 | `{Entity}Result` 반환 |
| `FoodCommand.Search` | `FoodQuery.Search` (별도 Query 클래스) |
| `@DomainService` 어노테이션 | `@Component` 어노테이션 |
| `BaseEntity.guard()` = abstract | `BaseEntity.guard()` = open (기본 Unit) |
| `EntityMapper` 미언급 | `EntityMapper<E, R>` 인터페이스로 Result 변환 |
| `BaseReader` 미언급 | `BaseReader<E, R>` 추상 클래스로 조회 공통화 |

---

## Design Decisions

### 하이브리드 아키텍처 채택 근거

기존 계획(Skills 7개)에서 **계층적 CLAUDE.md + Rules + Skills** 하이브리드로 변경한 이유:

| 메커니즘 | 로딩 시점 | 용도 |
|----------|----------|------|
| **계층적 CLAUDE.md** | 상위: 항상 / 하위: 해당 모듈 작업 시 자동 | 컨벤션, 아키텍처 결정사항 |
| **`.claude/rules/`** | path 패턴 매칭 파일 작업 시 자동 | 특정 파일 타입별 규칙 |
| **Skills** | 수동 호출 또는 Claude 판단 시 | 워크플로우, 절차적 지침 |
| **Agents** | 위임 시 격리된 컨텍스트 | 전문 작업 수행 |

**역할 분리 원칙:**
- 컨벤션/규칙 (what to do) → 계층적 CLAUDE.md + Rules (자동 로드)
- 워크플로우/절차 (how to do) → Skills (온디맨드)
- 전문 작업 (delegate) → Agents (격리 실행)

### 기존 계획 vs 최적화 계획

| 항목 | 기존 계획 | 최적화 계획 |
|------|----------|------------|
| CLAUDE.md | 1개 (루트만) | **4개 계층적** (루트+domain+app+api) |
| Rules | 없음 | **3개** path-specific |
| Skills | 7개 (컨벤션+워크플로우 혼합) | **3개** (순수 워크플로우만) |
| Agents | 3개 | 3개 (동일) |
| Jacoco | 없음 | **추가** (80% 커버리지 강제) |
| ktlint | 주석처리 | **활성화** |
| 워크플로우 반영 | 없음 | **7단계 워크플로우 완전 매핑** |
| 컨텍스트 효율 | Skills 7개 description 항상 로드 | 필요 시만 자동 로드 |

---

## 워크플로우 매핑

```
┌─────────┐    ┌──────────┐    ┌───────────┐    ┌──────────────┐    ┌──────────────┐    ┌───────────┐    ┌───────┐
│ 1.설계   │ →  │ 2.구현   │ →  │3.테스트작성│ →  │4.Jacoco 80% │ →  │ 5.코드리뷰  │ →  │ 6.리팩토링│ →  │7.커밋 │
└────┬────┘    └────┬─────┘    └─────┬─────┘    └──────┬──────┘    └──────┬───────┘    └─────┬─────┘    └───┬───┘
     │              │                │                 │                  │                   │              │
  /feature-dev   domain/         .claude/rules/     Hook 자동         /code-review       /simplify      git-master
  CLAUDE.md      CLAUDE.md       testing.md         (test 후 자동)    (OMC agent)        (OMC skill)    (OMC auto)
  (수동+자동)     (자동로드)      (자동로드)          (자동)            (수동호출)         (수동호출)     (자동감지)
     ▲              ▲                                                                       │
     │              │                                                                       │
     └──────────────┴───────────── 회귀 루프 (설계/구현 변경 필요 시) ◄──────────────────────┘
```

> **회귀 루프**: 6단계(리팩토링) 또는 5단계(코드리뷰) 결과에 따라 설계 변경(1단계)이나 구현 수정(2단계)이 필요할 수 있다.
> 이 경우 해당 단계로 회귀하여 3~6단계를 다시 순환한다. 태스크 상태도 회귀 단계부터 재추적한다.

### 단계 추적 프로토콜

`/feature-dev` 실행 시 TaskCreate로 7단계를 태스크로 생성하여 진행 상황을 추적한다.
- 각 단계 시작 시 해당 태스크를 `in_progress`로 변경 + "현재 **N단계: OO** 진행 중" announce
- 단계 완료 시 `completed`로 변경 + 다음 단계 자동 안내
- 특정 단계는 Hook으로 자동 실행 (아래 자동화 열 참조)

| 워크플로우 단계 | 지원 도구 | 로딩 방식 | 자동화 |
|---------------|----------|----------|--------|
| **1. 설계** | `/feature-dev` skill + 루트 `CLAUDE.md` | 수동 + 자동 | 태스크 리스트 자동 생성 |
| **2. 구현** | `domain/CLAUDE.md` + `application/CLAUDE.md` | 자동 | 모듈 진입 시 컨벤션 자동 로드 |
| **3. 테스트 작성** | `.claude/rules/testing.md` + `test-writer` agent | 자동 | 테스트 파일 작업 시 자동 |
| **4. Jacoco 80%** | `make coverage` | **Hook 자동** | 테스트 통과 시 Hook이 커버리지 자동 확인 |
| **5. 코드리뷰** | `/code-review` (OMC 기존 기능) | 수동 | 4단계 통과 후 자동 안내 |
| **6. 리팩토링** | `/simplify` (OMC) + `make lint` | 수동 | `make lint` 자동 실행. 설계/구현 변경 필요 시 1~2단계로 회귀 |
| **7. 커밋** | `git-master` (OMC 자동 감지) | **Hook 자동** | PreCommit Hook으로 lint 자동 검증 |

---

## Work Objectives

### Deliverables
1. Jacoco + ktlint 인프라 설정
2. 계층적 CLAUDE.md 4개 (루트 + domain + application + balance-eat-api)
3. Path-specific Rules 10개 (`.claude/rules/`)
4. Skills 3개 (워크플로우 전용)
5. Custom Agents 3개
6. Hooks/Settings 설정

### Definition of Done
- [ ] `claude/` 디렉토리 삭제 완료
- [ ] 기존 `CLAUDE.md` 삭제 완료
- [ ] Jacoco 설정 및 `make coverage` 동작 확인
- [ ] ktlint 활성화 및 `make lint` 동작 확인
- [ ] 새 루트 `CLAUDE.md` 200줄 이하
- [ ] 모듈별 CLAUDE.md 3개 생성
- [ ] Rules 10개 생성 (paths frontmatter 포함)
- [ ] Skills 3개 생성 (SKILL.md frontmatter 포함)
- [ ] Agents 3개 생성 (frontmatter 포함)
- [ ] `settings.json` 생성 (hooks + permissions)
- [ ] `make test` 통과
- [ ] `make coverage` 80%+ 통과
- [ ] 구식 패턴(DomainService, DTO) 미사용 확인

### Must NOT Have
- 구식 DomainService 패턴 언급
- 구식 DTO 패턴 언급 (Result가 대체)
- 200줄 초과 CLAUDE.md (각 파일 기준)
- 코드베이스에서 검증 불가능한 패턴 기술
- Skills에 컨벤션 내용 (컨벤션은 CLAUDE.md/Rules로)

---

## Task Flow

```
Phase 0 (Infra: Jacoco + ktlint)
    |
    v
Phase 1 (Cleanup: claude/ + CLAUDE.md 삭제)
    |
    v
Phase 2 (계층적 CLAUDE.md 4개)
    |
    v
Phase 3 (Rules 3개) ── CLAUDE.md 내용 참조하여 세부 규칙 작성
    |
    v
Phase 4 (Skills 3개) ─── Phase 5 (Agents 3개) [병렬 가능]
    |                          |
    └────────────┬─────────────┘
                 v
          Phase 6 (Hooks/Settings)
                 |
                 v
          Phase 7 (Verification)
```

---

## Phase 0: Infra Setup (Jacoco + ktlint 활성화)

**복잡도**: MEDIUM
**의존성**: 없음

### Task 0.1: Jacoco 플러그인 추가
- **경로**: `build.gradle.kts` (루트)
- **작업**:
  - `jacoco` 플러그인 subprojects에 적용
  - `jacocoTestReport` 태스크 설정 (xml + html)
  - `jacocoTestCoverageVerification` 태스크 설정 (minimum 0.80)
- **상세 설정**:
  ```kotlin
  subprojects {
      apply { plugin("jacoco") }

      jacoco { toolVersion = "0.8.12" }

      val jacocoExcludes = listOf(
          "**/config/**",
          "**/BalanceEatApplication*",
          "**/*Result*",
          "**/*Command*",
          "**/*Query*",
          "**/*Payload*",
          "**/*Request*",
          "**/*Response*",
      )

      tasks.jacocoTestReport {
          dependsOn(tasks.test)
          reports {
              xml.required = true
              html.required = true
          }
          classDirectories.setFrom(
              files(classDirectories.files.map {
                  fileTree(it) { exclude(jacocoExcludes) }
              })
          )
      }

      tasks.jacocoTestCoverageVerification {
          dependsOn(tasks.jacocoTestReport)
          classDirectories.setFrom(
              files(classDirectories.files.map {
                  fileTree(it) { exclude(jacocoExcludes) }
              })
          )
          violationRules {
              rule {
                  limit {
                      minimum = "0.80".toBigDecimal()
                  }
              }
          }
      }
  }
  ```

### Task 0.2: ktlint 플러그인 활성화
- **경로**: `build.gradle.kts` (루트)
- **작업**: 주석 해제
  - `plugin("org.jlleitschuh.gradle.ktlint")` 주석 해제
- **경로**: `application/balance-eat-api/build.gradle.kts`, `application/balance-eat-admin-api/build.gradle.kts`
- **작업**: 해당 파일의 ktlint 주석도 해제

### Task 0.3: Makefile 타겟 추가
- **경로**: `Makefile`
- **추가 타겟**:
  ```makefile
  coverage: test ## 테스트 커버리지를 확인합니다 (80% 이상).
  	@echo "📊 Checking test coverage..."
  	@$(GRADLEW) jacocoTestReport jacocoTestCoverageVerification

  lint: ## 코드 스타일을 검사합니다.
  	@echo "🔍 Running ktlint..."
  	@$(GRADLEW) ktlintCheck

  lint-fix: ## 코드 스타일을 자동 수정합니다.
  	@echo "🔧 Fixing code style..."
  	@$(GRADLEW) ktlintFormat
  ```

**커밋**: `[build] Jacoco 커버리지 80% 강제 및 ktlint 활성화`

---

## Phase 1: Cleanup (기존 제거)

**복잡도**: LOW
**의존성**: 없음 (Phase 0과 병렬 가능)

### Task 1.1: claude/ 디렉토리 삭제
- **경로**: `claude/` (프로젝트 루트)
- **삭제 대상**:
  - `claude/project-structure.md`
  - `claude/feature-development-guide.md`
  - `claude/entity-guidelines.md`
  - `claude/fixture-guidelines.md`
  - `claude/controller-test-guidelines.md`

### Task 1.2: 기존 CLAUDE.md 삭제
- **경로**: `CLAUDE.md` (프로젝트 루트)

**커밋**: `[chore] claude/ 디렉토리 및 기존 CLAUDE.md 삭제`

---

## Phase 2: 계층적 CLAUDE.md (컨벤션 = 자동 로드)

**복잡도**: HIGH
**의존성**: Phase 1 완료

### Task 2.1: 루트 CLAUDE.md
- **경로**: `CLAUDE.md`
- **목표**: 200줄 이하, WHY/WHAT/HOW 프레임워크
- **내용 구조**:
  ```markdown
  # Balance-Eat Backend

  ## WHY: 프로젝트 개요
  - 식단 관리 애플리케이션 백엔드
  - Kotlin 2.0.20 + Spring Boot 3.4.4 + Java 21
  - 멀티모듈 Gradle 프로젝트 (10개 서브모듈)

  ## WHAT: 아키텍처

  ### 모듈 구조
  (간략 트리: common, domain, supports, client, application)

  ### 핵심 패턴 요약
  1. Reader/Writer 패턴 (DomainService 대신)
  2. Result + EntityMapper 패턴 (DTO 대신)
  3. guard() 패턴 (엔티티 유효성 검증)
  4. Query 패턴 (검색 조건 분리)

  ### 계층 구조
  Controller → AppService(유스케이스) → Reader/Writer/Component → Repository/Client

  ## HOW: 빌드 & 워크플로우

  ### 빌드 명령어
  make test / make coverage / make lint / make openapi / make build

  ### 개발 워크플로우
  설계 → 구현 → 테스트 → 커버리지 80%+ → 코드리뷰 → 리팩토링 → 커밋

  ### 네이밍 규칙
  (파일명 테이블: Entity, Command, Query, Result, Reader, Writer 등)

  ### 주의사항 (Gotchas)
  - 테이블명 예약어는 쌍따옴표
  - guard()는 open fun (abstract 아님)
  - 에러 메시지는 반드시 한국어
  - TestFixture는 src/testFixtures/kotlin
  - Result companion object는 EntityMapper 구현
  - NEW_ID = 0L

  ## Skills Reference
  - @.claude/skills/feature-dev/SKILL.md - 기능 개발 워크플로우
  - @.claude/skills/new-domain/SKILL.md - 새 도메인 생성 체크리스트
  - @.claude/skills/api-docs/SKILL.md - OpenAPI 문서화
  ```
- **Acceptance Criteria**: 200줄 이하, 구식 패턴 미언급

### Task 2.2: domain/CLAUDE.md
- **경로**: `domain/CLAUDE.md`
- **로딩**: domain/ 하위 파일 작업 시 자동 로드
- **내용 구조**:
  ```markdown
  # Domain Module Conventions

  ## Reader/Writer 패턴
  - {Entity}Reader: @Component, @Transactional(readOnly=true), BaseReader<E, R> 상속
  - {Entity}Writer: @Component, @Transactional
  - Reader는 Result 반환, Writer도 Result 반환
  - BaseReader<E, R> 공통 메서드: get(), getAll(), findById() 등

  ## Result + EntityMapper 패턴
  - {Entity}Result: data class, 엔티티의 모든 필드 포함
  - companion object : EntityMapper<Entity, EntityResult> 구현
  - from(entity: Entity): EntityResult 변환 메서드
  - 도메인 엔티티는 외부 노출 금지

  ## Entity + guard() 패턴
  - BaseEntity 상속 필수
  - guard(): open fun (기본 Unit, override 가능)
  - @PrePersist/@PreUpdate에서 자동 호출
  - require()로 검증, 한국어 에러 메시지
  - NEW_ID = 0L 상수 사용
  - Enum은 @Enumerated(EnumType.STRING)

  ## Query 패턴
  - {Entity}Query.kt: 검색 조건 전용
  - Command는 CUD 전용, Query는 조회 전용
  - QueryDSL RepositoryCustom + Impl 패턴

  ## Command 패턴
  - {Entity}Command.kt: Create, Update 등 nested data class
  - 생성/수정에 필요한 필드만 포함

  ## 패키지 구조
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

### Task 2.3: application/CLAUDE.md
- **경로**: `application/CLAUDE.md`
- **로딩**: application/ 하위 파일 작업 시 자동 로드
- **내용 구조**:
  ```markdown
  # Application Layer Conventions

  ## Application Service (유스케이스 계층)
  - {Entity}Service: @Service 어노테이션
  - 클린 아키텍처의 유스케이스 = 일종의 main문
  - 다양한 컴포넌트(Reader, Writer, Sender, Client 등)를 주입받아 오케스트레이션
  - 자체 비즈니스 로직 최소화 — 로직은 도메인 엔티티나 전용 컴포넌트에 위임
  - Request → Command 변환 담당
  - 권한 검증 담당

  ## API 계층 구조
  - {Domain}V1ApiSpec.kt: API 명세 인터페이스 (OpenAPI 어노테이션)
  - {Domain}V1Controller.kt: @RestController, V1ApiSpec 구현
  - {Domain}V1Payload.kt: Request/Response nested data class

  ## 표준 응답
  - ApiResponse<T>: 표준 응답 래퍼
  - 예외 처리: ApplicationException, DomainException, DomainStatus enum

  ## URL 규칙
  - /api/v1/{domain} 경로 사용
  - RESTful 설계 원칙 준수
  ```

### Task 2.4: application/balance-eat-api/CLAUDE.md
- **경로**: `application/balance-eat-api/CLAUDE.md`
- **로딩**: balance-eat-api/ 하위 파일 작업 시 자동 로드
- **내용 구조**:
  ```markdown
  # Balance-Eat API Conventions

  ## Controller 테스트 패턴
  - @WebMvcTest + ControllerTestContext 상속
  - @MockkBean(relaxed = true)로 서비스 모킹
  - 성공 케이스만 테스트 (success() 메서드)
  - Nested 클래스로 API별 그룹화

  ## RestDocs 통합
  - restdocs-api-spec으로 OpenAPI3 생성
  - Tags enum: tagName, descriptionWith("작업명")
  - document() + ResourceSnippetParametersBuilder

  ## 헬퍼 메서드 문법
  - "field" type TYPE means "설명"
  - "field" type STRING means "설명" isOptional true
  - "field" type STRING means "설명" withEnum Enum::class
  - "id" pathMeans "설명"
  - "param" queryMeans "설명"
  - fieldsWithBasic(...)로 기본 응답 필드 포함

  ## Mock 원칙
  - every { service.method(any()) } returns mockData()
  - 해당 테스트에서 호출되는 메서드만 Mock 설정
  - 불필요한 Mock 금지

  ## OpenAPI 생성
  make openapi 또는 ./gradlew :application:balance-eat-api:openapi3
  ```

**커밋**: `[docs] 계층적 CLAUDE.md 구조 생성 (루트 + domain + application + api)`

---

## Phase 3: Path-specific Rules (.claude/rules/)

**복잡도**: MEDIUM
**의존성**: Phase 2 완료 (CLAUDE.md 내용을 참조하여 세부 규칙 작성)

### Task 3.1: testing.md
- **경로**: `.claude/rules/testing.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "**/test/**/*.kt"
    - "**/testFixtures/**/*.kt"
  ---
  ```
  - TestFixture<T> 인터페이스 패턴 (create(): T)
  - Entity Fixture: `{Entity}Fixture.kt` (var 필드, 기본값, create())
  - Command Fixture: `{Entity}CommandFixture.kt` (nested class 패턴)
  - Payload Fixture: `{Entity}V1PayloadFixture.kt` (Request/Response 통합)
  - 위치: `src/testFixtures/kotlin/org/balanceeat/domain/{domain}/`
  - 기본값: 현실적인 한국어 데이터
  - MutableList 사용 (컬렉션 필드)
  - 검증 패턴: `usingRecursiveComparison().ignoringFields(...)`
  - 통합 테스트: IntegrationTestContext 상속 (Testcontainers PostgreSQL)
  - 컨트롤러 테스트: ControllerTestContext 상속 (WebMvcTest)
  - 테스트 메서드명: 한국어 백틱 (`` `사용자를 생성할 수 있다` ``)

### Task 3.2: entity.md
- **경로**: `.claude/rules/entity.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "domain/src/main/**/domain/**/*.kt"
  ---
  ```
  - BaseEntity 상속, guard() 는 open fun
  - @PrePersist/@PreUpdate 자동 호출
  - require()로 한국어 에러 메시지
  - @Enumerated(EnumType.STRING)
  - NEW_ID = 0L
  - 테이블명 예약어 쌍따옴표 처리
  - ID: val, 비즈니스 필드: var
  - 선택적 필드: nullable + 기본값 null

### Task 3.3: api-controller.md
- **경로**: `.claude/rules/api-controller.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "application/**/api/**/*.kt"
    - "application/**/controller/**/*.kt"
  ---
  ```
  - V1ApiSpec 인터페이스 분리
  - V1Controller는 V1ApiSpec 구현
  - V1Payload: Request/Response nested data class
  - @MockkBean(relaxed = true)
  - RestDocs 헬퍼 문법 (type/means/withEnum/isOptional/fieldsWithBasic)

### Task 3.4: reader.md
- **경로**: `.claude/rules/reader.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "domain/src/main/**/*Reader.kt"
  ---
  ```
  - {Entity}Reader: `@Component`, `@Transactional(readOnly = true)`
  - `BaseReader<E, R>` 상속 필수
  - BaseReader 공통 메서드: `get()`, `getAll()`, `findById()` 등 — 불필요한 재정의 금지
  - 항상 Result 타입 반환 (Entity 직접 노출 금지)
  - 다른 도메인의 Reader 주입 가능 (조회는 도메인 경계 허용)
  - Writer를 주입하지 않음 (단방향 의존)

### Task 3.5: writer.md
- **경로**: `.claude/rules/writer.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "domain/src/main/**/*Writer.kt"
  ---
  ```
  - **역할**: 도메인 쓰기의 **원자적 단위** — 하나의 쓰기 유스케이스를 한 트랜잭션으로 묶음
  - {Entity}Writer: `@Component`, `@Transactional`
  - Result 타입 반환
  - 메서드명: `create()`, `update()`, `delete()` 등 명확한 CUD 동사 (save 아님)
  - create/update 내에 부가 로직(히스토리 로그 등) 포함 가능 — 같은 트랜잭션으로 원자성 보장
  - 다른 도메인의 Writer 주입 금지 (도메인 경계 보호)
  - 같은 도메인의 Reader 주입 가능
  - Reader/Writer 간 순환 참조 금지

### Task 3.6: service.md
- **경로**: `.claude/rules/service.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "application/src/main/**/*Service.kt"
    - "application/**/src/main/**/*Service.kt"
  ---
  ```
  - **역할**: 클린 아키텍처의 **유스케이스(Use Case) 계층** — 일종의 main문
  - `@Service` 어노테이션 사용
  - **오케스트레이터 패턴**: 다양한 컴포넌트(Reader, Writer, Sender, Client 등)를 주입받아 조율
    - 예: `ReminderScheduleService`는 `ReminderReader` + `NotificationDeviceReader` + `AppPushSender`를 조합
    - 예: `DietScheduleService`는 `DietReader` + `NotificationDeviceReader` + `AppPushSender`를 조합
  - **자체 비즈니스 로직 최소화**: Service에 복잡한 로직을 넣지 않음. 로직은 도메인 엔티티나 전용 컴포넌트에 위임
  - Request → Command 변환 담당
  - 권한 검증 담당 (인증된 사용자 확인)
  - 트랜잭션 범위: Service 레벨에서 `@Transactional` 선언하지 않음 (각 컴포넌트가 자체 관리)
  - 다른 도메인의 Service 직접 주입 금지 → 해당 도메인의 Reader/Writer/Component 사용

### Task 3.7: command.md
- **경로**: `.claude/rules/command.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "domain/src/main/**/*Command.kt"
  ---
  ```
  - `{Entity}Command.kt`: CUD 전용 입력 객체
  - nested data class 패턴: `Create`, `Update` 등
  - 생성/수정에 필요한 필드만 포함 (최소 원칙)
  - 불변 객체: `val` 필드만 사용
  - 검색 조건은 Command가 아닌 Query에 작성

### Task 3.8: result.md
- **경로**: `.claude/rules/result.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "domain/src/main/**/*Result.kt"
  ---
  ```
  - `{Entity}Result`: data class, 엔티티의 외부 노출용 필드 포함
  - `companion object : EntityMapper<Entity, EntityResult>` 구현 필수
  - `from(entity: Entity): EntityResult` 변환 메서드
  - 도메인 엔티티를 외부에 직접 노출하지 않음 — 반드시 Result로 변환
  - 연관 엔티티도 각각의 Result로 변환하여 포함
  - DTO 대신 Result 용어 사용 (프로젝트 컨벤션)

### Task 3.9: query.md
- **경로**: `.claude/rules/query.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "domain/src/main/**/*Query.kt"
  ---
  ```
  - `{Entity}Query.kt`: 검색/조회 조건 전용
  - nested data class 패턴: `Search`, `Filter` 등
  - Command와 분리: Command는 CUD, Query는 조회
  - QueryDSL `RepositoryCustom` + `Impl` 패턴과 함께 사용
  - 페이징 파라미터 포함 가능 (Pageable 연동)

### Task 3.10: payload.md
- **경로**: `.claude/rules/payload.md`
- **내용**:
  ```yaml
  ---
  paths:
    - "application/**/api/**/*Payload.kt"
    - "application/**/*Payload.kt"
  ---
  ```
  - `{Entity}V1Payload.kt`: API 계층의 Request/Response 객체
  - nested data class 패턴: `Request`, `Response` 등
  - Request → Command 변환은 Service에서 담당
  - Result → Response 변환은 Payload 내 companion/확장 함수로
  - validation 어노테이션 (`@NotBlank`, `@Size` 등)은 Request에 선언
  - OpenAPI 어노테이션 (`@Schema`)은 V1ApiSpec 인터페이스에 선언

**커밋**: `[docs] .claude/rules path-specific 규칙 10개 추가`

---

## Phase 4: Skills (워크플로우 전용)

**복잡도**: MEDIUM
**의존성**: Phase 2 완료

**핵심 원칙**: 컨벤션은 CLAUDE.md + Rules로 이동 완료. Skills는 **순수 워크플로우(절차)만** 담당.

### Task 4.1: /feature-dev Skill
- **경로**: `.claude/skills/feature-dev/SKILL.md`
- **Frontmatter**:
  ```yaml
  ---
  name: feature-dev
  description: 기능 개발 전체 워크플로우. 설계→구현→테스트→커버리지→리뷰→리팩토링→커밋 7단계.
  disable-model-invocation: true
  ---
  ```
- **내용**:
  - **단계 추적 프로토콜**:
    - 워크플로우 시작 시 TaskCreate로 7단계를 태스크로 생성
    - 각 단계 시작 시 해당 태스크를 `in_progress`로 변경 + "현재 **N단계: OO** 진행 중" announce
    - 단계 완료 시 `completed`로 변경 + 다음 단계 자동 안내
    - 모든 단계 완료 시 최종 체크리스트 확인
  - 7단계 워크플로우 (유저 실제 워크플로우 반영):
    1. **설계**: 요구사항 분석, 영향 범위 파악, 기존 코드 탐색
    2. **구현**: domain-builder agent 활용 또는 직접 구현
    3. **테스트 작성**: test-writer agent 활용, Fixture 생성, 통합/컨트롤러 테스트
    4. **커버리지 확인**: 테스트 통과 시 Hook이 `make coverage` 자동 실행 → 80%+ 확인
    5. **코드리뷰**: 4단계 통과 확인 후 `/code-review` (OMC agent) 호출 안내
    6. **리팩토링**: `/simplify` + `make lint` + 리뷰 피드백 반영
    7. **커밋**: PreCommit Hook이 lint 자동 검증 → 변경사항 커밋
  - **자동 전환 규칙**:
    - 3단계(테스트) 완료 → Hook이 `make coverage` 자동 실행
    - 4단계(커버리지) 80%+ 확인 → 자동으로 `/code-review` 안내
    - 6단계(리팩토링) 완료 → 자동으로 `make lint` 실행
  - **회귀 루프 규칙**:
    - 5단계(코드리뷰) 또는 6단계(리팩토링)에서 설계 변경이 필요하면 → 1단계(설계)로 회귀
    - 구현 수정만 필요하면 → 2단계(구현)로 회귀
    - 회귀 시 해당 단계부터 태스크 상태를 `in_progress`로 재설정하고 3~7단계를 다시 순환
    - 회귀 발생 시 announce: "**회귀: N단계로 돌아갑니다** (사유: OO)"
  - 각 단계별 사용 도구와 명령어
  - 단계 완료 체크리스트

### Task 4.2: /new-domain Skill
- **경로**: `.claude/skills/new-domain/SKILL.md`
- **Frontmatter**:
  ```yaml
  ---
  name: new-domain
  description: 새 도메인 생성 종합 체크리스트. 완전히 새로운 도메인을 추가할 때 사용. $ARGUMENTS로 도메인명 전달.
  disable-model-invocation: true
  ---
  ```
- **내용**:
  - $ARGUMENTS로 도메인명 받기
  - 13단계 생성 순서:
    1. Entity.kt (BaseEntity 상속, guard())
    2. {Entity}Result.kt (EntityMapper 구현)
    3. {Entity}Command.kt (Create, Update)
    4. {Entity}Query.kt (Search 조건)
    5. {Entity}Repository.kt (JpaRepository)
    6. {Entity}RepositoryCustom.kt + Impl (QueryDSL)
    7. {Entity}Reader.kt (BaseReader 상속)
    8. {Entity}Writer.kt (@Component)
    9. {Entity}Service.kt (유스케이스 오케스트레이터)
    10. {Entity}V1Payload.kt (Request/Response)
    11. {Entity}V1ApiSpec.kt (OpenAPI 인터페이스)
    12. {Entity}V1Controller.kt
    13. Fixtures + Tests
  - 파일 경로 템플릿
  - 패키지: `org.balanceeat.domain.{domain}/`, `org.balanceeat.api.{domain}/`

### Task 4.3: /api-docs Skill
- **경로**: `.claude/skills/api-docs/SKILL.md`
- **Frontmatter**:
  ```yaml
  ---
  name: api-docs
  description: OpenAPI3 문서 생성 및 검증 워크플로우. OpenAPI/Swagger/RestDocs 관련 작업 시 자동 호출.
  ---
  ```
  > Note: `disable-model-invocation` 제거 — OpenAPI 관련 작업 시 Claude가 자동으로 호출할 수 있도록 허용.
- **내용**:
  - `make openapi` 실행 절차
  - 생성된 YAML 검증
  - Tags enum 등록 방법
  - HTTP 테스트 파일 작성 규칙

**커밋**: `[docs] Claude Code Skills 3개 생성 (워크플로우 전용)`

---

## Phase 5: Custom Agents (.claude/agents/)

**복잡도**: MEDIUM
**의존성**: Phase 3 완료 (Phase 4와 병렬 가능)

### Task 5.1: kotlin-reviewer Agent
- **경로**: `.claude/agents/kotlin-reviewer.md`
- **Frontmatter**:
  ```yaml
  ---
  name: kotlin-reviewer
  description: Kotlin/Spring Boot 코드 리뷰어. Reader/Writer, Result, guard() 패턴 준수 여부를 검증한다. 코드 변경 후 사용 권장.
  model: sonnet
  ---
  ```
  > Note: `tools:` 필드는 Claude Code custom agent frontmatter 공식 스펙에 없음. 도구 제한은 system prompt 내에서 "Read, Grep, Glob, Bash만 사용하라"고 지시.
- **System Prompt**:
  - Reader/Writer 패턴 준수 확인
  - Result + EntityMapper 패턴 준수 확인
  - guard() 검증 로직 확인
  - 한국어 에러 메시지 사용 확인
  - 네이밍 규칙 준수 확인
  - @Transactional 적절성 확인
  - 리뷰 결과를 severity (Critical/Warning/Suggestion)로 분류
  - 코드 수정하지 않음 (읽기 전용)

### Task 5.2: test-writer Agent
- **경로**: `.claude/agents/test-writer.md`
- **Frontmatter**:
  ```yaml
  ---
  name: test-writer
  description: 프로젝트 패턴에 맞는 테스트 작성 전문 에이전트. TestFixture, IntegrationTestContext, ControllerTestContext 활용.
  model: sonnet
  ---
  ```
  > Note: 도구 제한은 system prompt 내에서 "Read, Write, Edit, Grep, Glob, Bash만 사용하라"고 지시.
- **System Prompt**:
  - TestFixture<T> 패턴으로 Fixture 생성
  - 통합 테스트: IntegrationTestContext 상속
  - 컨트롤러 테스트: ControllerTestContext 상속, 성공 케이스만
  - usingRecursiveComparison() 검증 패턴
  - 한국어 백틱 테스트 메서드명
  - RestDocs 헬퍼 메서드 활용
  - mockk/springmockk 사용
  - Fixture는 src/testFixtures/kotlin에 위치
  - 테스트 작성 후 `./gradlew test` 실행하여 확인

### Task 5.3: domain-builder Agent
- **경로**: `.claude/agents/domain-builder.md`
- **Frontmatter**:
  ```yaml
  ---
  name: domain-builder
  description: 새 도메인 일괄 생성 에이전트. Entity→Result→Command→Query→Repository→Reader→Writer + Fixtures 생성.
  model: sonnet
  ---
  ```
  > Note: `tools:`, `skills:` 필드는 공식 스펙에 없음. system prompt에서 "Read, Write, Edit, Grep, Glob, Bash를 사용하고, /new-domain Skill을 참조하라"고 지시.
- **System Prompt**:
  - new-domain Skill의 13단계 체크리스트 순서대로 실행
  - $ARGUMENTS로 도메인명과 필드 정보 받기
  - 기존 도메인(예: Food, User)의 구조를 참조하여 일관성 유지
  - guard() 검증 로직 자동 생성
  - EntityMapper 자동 구현
  - Fixture 자동 생성
  - 완료 후 `./gradlew :domain:test` 실행

**커밋**: `[docs] Claude Code Custom Agents 3개 생성`

---

## Phase 6: Hooks & Settings

**복잡도**: MEDIUM
**의존성**: Phase 4, 5 완료

### Task 6.1: .claude/settings.json 생성
- **경로**: `.claude/settings.json`
- **내용**:
  ```json
  {
    "permissions": {
      "allow": [
        "Bash(make *)",
        "Bash(./gradlew *)"
      ]
    },
    "hooks": {
      "PostToolUse": [
        {
          "matcher": "Bash(make test|./gradlew test|./gradlew.*test)",
          "hooks": [
            {
              "type": "command",
              "command": "cd $PROJECT_DIR && make coverage 2>&1 | tail -5"
            }
          ]
        }
      ],
      "PreCommit": [
        {
          "hooks": [
            {
              "type": "command",
              "command": "cd $PROJECT_DIR && make lint 2>&1 | tail -5"
            }
          ]
        }
      ]
    }
  }
  ```
- **설계 근거**:
  - **PostToolUse + test matcher**: 테스트 실행 후 Jacoco 커버리지 자동 확인 (4단계 자동화)
  - **PreCommit**: 커밋 전 ktlint 자동 검증 (7단계 품질 게이트)
  - permissions: make, gradlew 명령어 자동 허용
  - `settings.local.json`과의 관계: `settings.json`(팀 공유, git tracked)과 `settings.local.json`(개인, git ignored)은 Claude Code가 자동 merge 처리. `settings.local.json`의 기존 `./gradlew:*` permission은 유지됨

**커밋**: `[chore] Claude Code hooks 및 settings.json 설정`

---

## Phase 7: Verification

**복잡도**: LOW
**의존성**: Phase 0-6 모두 완료

### Task 7.1: 파일 존재 확인
예상 파일 목록:
- `CLAUDE.md` (루트)
- `domain/CLAUDE.md`
- `application/CLAUDE.md`
- `application/balance-eat-api/CLAUDE.md`
- `.claude/rules/testing.md`
- `.claude/rules/entity.md`
- `.claude/rules/api-controller.md`
- `.claude/skills/feature-dev/SKILL.md`
- `.claude/skills/new-domain/SKILL.md`
- `.claude/skills/api-docs/SKILL.md`
- `.claude/agents/kotlin-reviewer.md`
- `.claude/agents/test-writer.md`
- `.claude/agents/domain-builder.md`
- `.claude/settings.json`

### Task 7.2: CLAUDE.md 줄 수 확인
- `wc -l CLAUDE.md` → 200줄 이하
- `wc -l domain/CLAUDE.md` → 150줄 이하
- `wc -l application/CLAUDE.md` → 100줄 이하
- `wc -l application/balance-eat-api/CLAUDE.md` → 100줄 이하

### Task 7.3: 빌드 및 커버리지 확인
- `make test` 통과
- `make coverage` 80%+ 통과
- `make lint` 통과 (또는 경고만)

### Task 7.4: 구식 패턴 미사용 확인
```bash
grep -r "DomainService\|@DomainService" CLAUDE.md domain/CLAUDE.md application/ .claude/
# 결과 없어야 함
```

### Task 7.5: claude/ 디렉토리 삭제 확인
```bash
ls claude/ 2>&1  # "No such file or directory" 나와야 함
```

---

## Commit Strategy

| 순서 | Phase | 커밋 메시지 | 푸시 |
|------|-------|-----------|------|
| 1 | Phase 0 | `[build] Jacoco 커버리지 80% 강제 및 ktlint 활성화` | Yes |
| 2 | Phase 1 | `[chore] claude/ 디렉토리 및 기존 CLAUDE.md 삭제` | Yes |
| 3 | Phase 2 | `[docs] 계층적 CLAUDE.md 구조 생성` | Yes |
| 4 | Phase 3 | `[docs] .claude/rules path-specific 규칙 추가` | Yes |
| 5 | Phase 4 | `[docs] Claude Code Skills 3개 생성 (워크플로우 전용)` | Yes |
| 6 | Phase 5 | `[docs] Claude Code Custom Agents 3개 생성` | Yes |
| 7 | Phase 6 | `[chore] Claude Code hooks 및 settings.json 설정` | Yes |

---

## Success Criteria

| 기준 | 검증 방법 |
|------|----------|
| `claude/` 삭제됨 | `ls claude/` 실패 |
| 루트 CLAUDE.md 200줄 이하 | `wc -l CLAUDE.md` |
| 계층적 CLAUDE.md 4개 | `ls CLAUDE.md domain/CLAUDE.md application/CLAUDE.md application/balance-eat-api/CLAUDE.md` |
| Rules 10개 존재 | `ls .claude/rules/*.md` |
| Skills 3개 존재 | `ls .claude/skills/*/SKILL.md` |
| Agents 3개 존재 | `ls .claude/agents/*.md` |
| settings.json 존재 | `cat .claude/settings.json` |
| 구식 패턴 미사용 | `grep -r "DomainService" .claude/ CLAUDE.md domain/CLAUDE.md` 결과 없음 |
| Jacoco 동작 | `make coverage` 성공 |
| ktlint 동작 | `make lint` 성공 |
| 테스트 통과 | `make test` 성공 |
| 워크플로우 매핑 완료 | 7단계 모두 도구 매핑됨 |
