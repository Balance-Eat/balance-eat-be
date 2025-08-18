# Balance-Eat Project Structure Guidelines

## Overview

Balance-Eat은 멀티모듈 Gradle 프로젝트로 구성된 식단 관리 애플리케이션입니다. 도메인 주도 설계(DDD) 원칙을 따릅니다.

## Project Architecture

```
balance-eat/
├── application/           # Application layer (외부 인터페이스)
│   └── balance-eat-api/  # REST API 애플리케이션
├── domain/               # Domain layer (핵심 비즈니스 로직)
├── supports/             # Supporting modules (공통 유틸리티)
│   ├── jackson/         # JSON 직렬화 설정
│   └── monitoring/      # 모니터링 설정
├── claude/              # 프로젝트 문서화
└── http/                # API 테스트 파일
```

## Module Structure

### 1. Application Module (`application/balance-eat-api/`)

웹 API를 제공하는 Spring Boot 애플리케이션입니다.

```
src/main/kotlin/org/balanceeat/api/
├── BalanceEatApiApplication.kt    # Spring Boot 메인 클래스
├── config/                        # 설정 클래스들
│   ├── ApiResponse.kt            # API 응답 표준화
│   ├── CorsConfig.kt             # CORS 설정
│   ├── DatabaseConfig.kt         # 데이터베이스 설정
│   ├── GlobalExceptionHandler.kt # 전역 예외 처리
│   ├── SwaggerConfig.kt          # API 문서화
│   └── WebConfig.kt              # 웹 설정
├── controller/
│   └── HealthController.kt       # 헬스체크 엔드포인트
├── user/                         # 사용자 관련 API
│   ├── UserV1ApiSpec.kt          # API 명세 (OpenAPI)
│   ├── UserV1Controller.kt       # 컨트롤러
│   └── UserV1Payload.kt          # 요청/응답 DTO
└── diet/                         # 식단 관련 API
    ├── DietV1ApiSpec.kt          # API 명세
    ├── DietV1Controller.kt       # 컨트롤러
    └── DietV1Payload.kt          # 요청/응답 DTO
```

**핵심 컨벤션**:
- 컨트롤러는 `{Domain}V1Controller` 명명
- API 명세는 `{Domain}V1ApiSpec` 인터페이스로 분리
- 요청/응답 객체는 `{Domain}V1Payload`에 모음
- 모든 API 엔드포인트는 `/api/v1/{domain}` 경로 사용

### 2. Domain Module (`domain/`)

핵심 비즈니스 로직과 도메인 모델을 포함합니다.

```
src/main/kotlin/org/balanceeat/domain/
├── common/                       # 공통 예외 및 상태
│   ├── ApplicationException.kt   # 애플리케이션 예외 기반 클래스
│   ├── DomainException.kt        # 도메인 예외 기반 클래스
│   ├── DomainStatus.kt           # 도메인 상태 enum
│   ├── Status.kt                 # 응답 상태 인터페이스
│   └── exceptions/               # 구체적인 예외 클래스들
├── config/                       # 도메인 설정
│   ├── BaseEntity.kt            # JPA 기본 엔터티
│   └── JpaConfig.kt             # JPA 설정
├── user/                        # 사용자 도메인
│   ├── User.kt                  # 사용자 엔터티
│   ├── UserCommand.kt           # 사용자 명령 객체
│   ├── UserDomainService.kt     # 사용자 도메인 서비스
│   └── UserRepository.kt        # 사용자 리포지토리 인터페이스
├── food/                        # 음식 도메인
│   ├── Food.kt                  # 음식 엔터티
│   ├── FoodCommand.kt           # 음식 명령 객체
│   ├── FoodDomainService.kt     # 음식 도메인 서비스
│   └── FoodRepository.kt        # 음식 리포지토리 인터페이스
└── diet/                        # 식단 도메인
    ├── Diet.kt                  # 식단 엔터티
    ├── DietFood.kt             # 식단-음식 연결 엔터티
    ├── DietCommand.kt          # 식단 명령 객체
    ├── DietDomainService.kt    # 식단 도메인 서비스
    ├── DietRepository.kt       # 식단 리포지토리 인터페이스
    └── DietFoodRepository.kt   # 식단-음식 리포지토리 인터페이스
```

**도메인 모델 컨벤션**:
- **Entity**: `{Domain}.kt` - JPA 엔터티 (@Entity)
- **Command**: `{Domain}Command.kt` - 명령 객체 (Create, Update 등)
- **Service**: `{Domain}DomainService.kt` - 도메인 비즈니스 로직
- **Repository**: `{Domain}Repository.kt` - 데이터 접근 인터페이스

### 3. Test Structure

도메인 모듈의 테스트 구조:

```
src/test/kotlin/org/balanceeat/domain/
├── TestDomainApplication.kt      # 테스트용 Spring Boot 애플리케이션
├── config/                       # 테스트 설정
│   ├── FixtureReflectionUtils.kt # Fixture 리플렉션 유틸리티
│   ├── TestFixture.kt           # Fixture 인터페이스
│   ├── MySQLTestContainerConfig.kt # MySQL 테스트 컨테이너
│   ├── PostgreSQLTestContainerConfig.kt # PostgreSQL 테스트 컨테이너
│   └── supports/
│       ├── CleanUp.kt           # 테스트 데이터 정리
│       └── IntegrationTestContext.kt # 통합 테스트 기반 클래스
├── user/                        # 사용자 도메인 테스트
│   ├── UserFixture.kt          # 사용자 엔터티 픽스처
│   ├── UserCommandFixture.kt   # 사용자 명령 픽스처
│   └── UserDomainServiceTest.kt # 사용자 도메인 서비스 테스트
├── food/                        # 음식 도메인 테스트  
│   ├── FoodFixture.kt          # 음식 엔터티 픽스처
│   └── FoodCommandFixture.kt   # 음식 명령 픽스처
└── diet/                        # 식단 도메인 테스트
    ├── DietFixture.kt          # 식단 엔터티 픽스처
    ├── DietCommandFixture.kt   # 식단 명령 픽스처
    ├── DietFoodFixture.kt      # 식단-음식 픽스처
    └── DietDomainServiceTest.kt # 식단 도메인 서비스 테스트
```

**테스트 컨벤션**:
- **Fixture**: `{Entity}Fixture.kt` - 테스트 데이터 생성
- **Test**: `{Domain}DomainServiceTest.kt` 또는 `{Domain}IntegrationTest.kt`
- **Base Class**: `IntegrationTestContext` 상속하여 통합 테스트 환경 구성

### 4. Supporting Modules (`supports/`)

공통 기능을 제공하는 모듈들:

#### Jackson Module (`supports/jackson/`)
- JSON 직렬화/역직렬화 설정
- `JacksonConfig.kt`: ObjectMapper 설정

#### Monitoring Module (`supports/monitoring/`)
- 애플리케이션 모니터링 설정
- `MonitoringConfig.kt`: 메트릭 및 로깅 설정

## File Naming Conventions

### Source Files
- **Entity**: `{Domain}.kt` (예: `User.kt`, `Food.kt`)
- **Command**: `{Domain}Command.kt` (예: `UserCommand.kt`)
- **Service**: `{Domain}DomainService.kt` (예: `UserDomainService.kt`)
- **Repository**: `{Domain}Repository.kt` (예: `UserRepository.kt`)
- **Controller**: `{Domain}V1Controller.kt` (예: `UserV1Controller.kt`)
- **Payload**: `{Domain}V1Payload.kt` (예: `UserV1Payload.kt`)
- **API Spec**: `{Domain}V1ApiSpec.kt` (예: `UserV1ApiSpec.kt`)

### Test Files
- **Fixture**: `{Entity}Fixture.kt` (예: `UserFixture.kt`)
- **Command Fixture**: `{Domain}CommandFixture.kt` (예: `UserCommandFixture.kt`)
- **Test**: `{Domain}DomainServiceTest.kt` 또는 `{Domain}IntegrationTest.kt`

### Configuration Files
- **Application**: `application.yml`, `application-{profile}.yml`
- **Environment**: `{env}.env` (예: `local.env`, `prod.env`)
- **Docker**: `docker-compose.yml`, `Dockerfile`
- **HTTP**: `{domain}-api.http` (예: `user-api.http`)

## Package Structure Conventions

### Java Package Naming
모든 패키지는 `org.balanceeat.{module}` 형태로 시작:
- `org.balanceeat.api` - API 애플리케이션
- `org.balanceeat.domain` - 도메인 모델
- `org.balanceeat.jackson` - Jackson 설정
- `org.balanceeat.monitoring` - 모니터링 설정

### Domain Package Organization
도메인별로 패키지를 분리하여 관련 클래스들을 함께 묶습니다:
- `org.balanceeat.domain.user` - 사용자 관련 모든 클래스
- `org.balanceeat.domain.food` - 음식 관련 모든 클래스  
- `org.balanceeat.domain.diet` - 식단 관련 모든 클래스
- `org.balanceeat.domain.common` - 도메인 공통 클래스
- `org.balanceeat.domain.config` - 도메인 설정 클래스

## Development Standards

### Code Organization
- **Domain-Driven**: 도메인별로 모든 관련 클래스를 같은 패키지에 위치
- **Layer Separation**: 각 계층(application, domain)은 명확히 분리
- **Dependency Direction**: 외부 계층은 내부 계층에 의존, 반대는 불가

### Testing Standards
- **Integration Testing**: 실제 데이터베이스와 Spring Context 사용
- **Test Containers**: Docker 컨테이너로 실제 데이터베이스 환경 구성
- **Fixture Pattern**: 테스트 데이터 생성을 위한 표준화된 픽스처 사용

### API Standards
- **RESTful**: REST API 설계 원칙 준수
- **Versioning**: URL 경로에 버전 포함 (`/api/v1/`)
- **OpenAPI**: 모든 API는 OpenAPI 명세로 문서화
- **Standard Response**: 표준화된 응답 형식 사용

## Documentation Standards

### Code Documentation
- **Korean Comments**: 한국어로 주석 작성
- **Javadoc**: 공개 API는 Javadoc 스타일 문서화
- **README**: 각 모듈별 README.md 파일 유지

### Project Documentation
- **CLAUDE.md**: 프로젝트 메인 가이드라인
- **claude/ Directory**: 세부 가이드라인 문서들
- **API Documentation**: Swagger/OpenAPI 문서 자동 생성

이 구조 가이드라인을 따라 개발하면 일관성 있고 유지보수가 용이한 코드베이스를 유지할 수 있습니다.