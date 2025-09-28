# BalanceEat - 개인 맞춤형 영양 균형 관리 서비스

BalanceEat은 사용자의 개인 정보와 목표를 설정하고 식단을 기록하여 영양 균형을 관리할 수 있는 백엔드 서비스입니다.

## 🎯 주요 기능

- **식단 기록 및 통계**: 일별, 월별 식단 기록 및 영양 섭취 현황 조회 및 통계 제공
- **영양 균형 관리**: 탄수화물, 단백질, 지방 등 영양소 균형을 고려한 식단 계획
- **알림 기능**: 식사 시간 알림 및 영양 섭취 리마인더
- **음식 데이터베이스**: 다양한 음식의 영양 정보와 칼로리 데이터 제공
- **개인 맞춤형 목표 설정**: 사용자의 체중, 키, 활동량 등을 고려한 개인별 목표 설정


## 🛠 기술 스택

### Backend Framework
- **Kotlin** 2.0.20 - 주 개발 언어
- **Spring Boot** 3.4.4 - 애플리케이션 프레임워크
- **Spring Data JPA** - ORM 및 데이터 액세스

### Database
- **PostgreSQL** 14 - 메인 데이터베이스
- **QueryDSL** 7.0 - 타입 세이프 쿼리 작성

### Build & Tools
- **Gradle Kotlin DSL** - 빌드 도구
- **Java 21** - JVM 플랫폼
- **KSP (Kotlin Symbol Processing)** - 어노테이션 프로세싱

### Testing
- **JUnit 5** - 테스트 프레임워크
- **MockK** - Kotlin 전용 모킹 라이브러리
- **Spring MockK** - Spring과 MockK 통합
- **Testcontainers** - 통합 테스트용 컨테이너

### Documentation
- **Spring REST Docs** - API 문서 자동 생성
- **OpenAPI 3** - API 명세서 생성

## 🏗 멀티모듈 구조

프로젝트는 도메인 중심의 멀티모듈 아키텍처로 구성되어 있습니다.

```
balance-eat/
├── application/
│   ├── balance-eat-api/          # 메인 REST API 서버
│   ├── balance-eat-admin-api/    # 관리자 API 서버
│   └── api-base/                 # API 공통 모듈
├── domain/                       # 도메인 로직 및 엔티티
├── common/                       # 공통 status 혹은 유틸리티
└── supports/
    ├── jackson/                  # JSON 직렬화 설정
    └── monitoring/               # 모니터링 및 로깅
```

### 모듈별 역할

#### Application Layer
- **balance-eat-api**: 사용자 대상 REST API 제공
- **balance-eat-admin-api**: 관리자용 API 제공
- **api-base**: API 계층의 공통 기능 (컨트롤러 기본 설정, 예외 처리 등)

#### Domain Layer
- **domain**: 핵심 비즈니스 로직과 엔티티
  - 사용자(User) 도메인
  - 음식(Food) 도메인
  - 식단(Diet) 도메인
  - 큐레이션(Curation) 도메인

#### Infrastructure Layer
- **common**: 전체 모듈에서 사용하는 공통 status 혹은 유틸리티
- **supports**: logging, monitoring 과 같이 부가적인 기능을 지원하는 add-on 모듈
- **supports/jackson**: JSON 직렬화/역직렬화 설정
- **supports/monitoring**: 애플리케이션 모니터링 및 로깅

### 아키텍처 특징

- **도메인 중심 설계**: 각 도메인별로 독립적인 서비스 구조
- **분리된 모듈 구조**: 애플리케이션, 도메인, 인프라스트럭처 계층 분리
- **테스트 격리**: 각 모듈별 독립적인 테스트 환경
- **확장성**: 새로운 도메인이나 API 서버 추가 용이

## 🚀 시작하기

### 필수 요구사항
- Java 21+
- PostgreSQL 14+
- Gradle 8.0+

### 실행 방법
```bash
# 프로젝트 클론
git clone <repository-url>

# 의존성 설치 및 빌드
./gradlew build

# 메인 API 서버 실행
./gradlew :application:balance-eat-api:bootRun
```

## 📚 개발 가이드

프로젝트 개발 시 다음 문서들을 참고하세요:

- [프로젝트 구조 가이드](claude/project-structure.md)
- [엔티티 설계 가이드](claude/entity-guidelines.md)
- [기능 개발 가이드](claude/feature-development-guide.md)
- [테스트 픽스처 가이드](claude/fixture-guidelines.md)