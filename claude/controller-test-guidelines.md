# Controller Test Guidelines

Balance-Eat 프로젝트의 컨트롤러 테스트 작성 가이드라인입니다.

**⚠️ 주의: 이 가이드는 성공 응답 테스트만 다룹니다.**

## 테스트 범위

### ✅ 다루는 내용
- HTTP 200, 201 등 성공 응답 테스트
- 정상적인 요청/응답 시나리오
- API 문서화를 위한 RestDocs 통합
- Mock 설정과 테스트 데이터 준비

### ❌ 다루지 않는 내용
- 예외 처리 및 오류 응답 (4xx, 5xx)
- 검증 실패 케이스
- 권한 및 인증 오류
- 비즈니스 로직 예외 상황

## 목차

1. [테스트 구조](#테스트-구조)
2. [기본 패턴](#기본-패턴)
3. [테스트 클래스 작성](#테스트-클래스-작성)
4. [API 문서화](#api-문서화)
5. [헬퍼 메서드 활용](#헬퍼-메서드-활용)
6. [Mock 설정](#mock-설정)
7. [Request/Response 테스트](#requestresponse-테스트)
8. [Best Practices](#best-practices)

## 테스트 구조

### 기본 클래스 구조

```kotlin
@WebMvcTest(DomainV1Controller::class)
class DomainV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean(relaxed = true)
    private lateinit var domainService: DomainService

    // 테스트 메서드들...
    
    // 헬퍼 메서드들...
}
```

### 필수 어노테이션

- `@WebMvcTest`: 특정 컨트롤러만 로드하여 빠른 테스트 실행
- `ControllerTestContext` 상속: 공통 테스트 유틸리티 제공
- `@MockkBean(relaxed = true)`: 서비스 레이어 모킹

## 기본 패턴

### Nested 클래스를 이용한 API별 그룹화

```kotlin
@Nested
@DisplayName("POST /v1/domain - 도메인 생성")
inner class CreateTest {
    @Test
    fun success() {
        // 성공 케이스 테스트 구현
    }
}

@Nested
@DisplayName("GET /v1/domain/{id} - 도메인 조회")
inner class GetDetailsTest {
    @Test
    fun success() {
        // 성공 케이스 테스트 구현
    }
}

@Nested
@DisplayName("PUT /v1/domain/{id} - 도메인 수정")
inner class UpdateTest {
    @Test
    fun success() {
        // 성공 케이스 테스트 구현
    }
}

@Nested
@DisplayName("GET /v1/domain/recommendations - 도메인 추천 목록 조회")
inner class GetRecommendationsTest {
    @Test
    fun success() {
        // 성공 케이스 테스트 구현
    }
}
```

### 테스트 메서드 구조

```kotlin
@Test
fun success() {
    // Given: 테스트 데이터 준비
    val request = mockCreateRequest()
    every { domainService.create(any(), any()) } returns mockDomainDto()

    // When & Then: API 호출 및 검증
    given()
        .body(request)
        .post("/v1/domain")
        .then()
        .log().all()
        .apply(documentationHandler())
        .status(HttpStatus.CREATED)
}
```

## 테스트 클래스 작성

### 파일명 규칙
- `{Domain}V1ControllerTest.kt`
- 예: `FoodV1ControllerTest.kt`, `UserV1ControllerTest.kt`

### 테스트 메서드명 규칙
- 성공 케이스만 작성: `success()`

### Mock 설정 패턴

```kotlin
// 성공 케이스만 설정
every { domainService.create(any(), any()) } returns mockDomainDto()
every { domainService.getDetails(any()) } returns mockDomainDto()
every { domainService.update(any(), any(), any()) } returns mockDomainDto()
every { domainService.getRecommendations(any()) } returns listOf(mockDomainDto())
```

## API 문서화

### RestDocs 통합

**기본 패턴**: `Tags.{DOMAIN}.descriptionWith("작업명")`을 사용하여 일관된 설명 형식 유지

```kotlin
.apply(
    document(
        identifier("CreateTest"),
        ResourceSnippetParametersBuilder()
            .tag(Tags.FOOD.tagName)
            .description(Tags.FOOD.descriptionWith("생성")),
        responseFields(
            fieldsWithBasic(
                "data.id" type NUMBER means "음식 ID",
                "data.name" type STRING means "음식명",
                "data.createdAt" type STRING means "생성일시"
            )
        )
    )
)
```

**설명 패턴 예시**:
```kotlin
// 생성 API
.description(Tags.FOOD.descriptionWith("생성"))

// 수정 API  
.description(Tags.FOOD.descriptionWith("수정"))

// 상세 조회 API
.description(Tags.FOOD.descriptionWith("상세 조회"))

// 목록 조회 API
.description(Tags.FOOD.descriptionWith("목록 조회"))

// 추천 목록 조회 API
.description(Tags.FOOD.descriptionWith("추천 목록 조회"))
```

### Request Body 문서화

```kotlin
requestFields(
    "uuid" type STRING means "도메인 UUID",
    "name" type STRING means "도메인명",
    "description" type STRING means "설명 (선택사항)" isOptional true
)
```

#### isOptional 사용 원칙

**필수 vs 선택적 필드 구분**:
- `isOptional true`: Payload에서 nullable이거나 기본값이 있는 필드
- `isOptional` 생략: @NotNull 어노테이션이 있는 필수 필드

**올바른 사용 예시**:
```kotlin
requestFields(
    // 필수 필드 (isOptional 생략)
    "name" type STRING means "사용자 이름",
    "age" type NUMBER means "사용자 나이",
    
    // 선택적 필드 (isOptional true 명시)
    "email" type STRING means "이메일 주소 (선택)" isOptional true,
    "phone" type STRING means "연락처 (선택)" isOptional true
)
```

**responseFields에서도 적용**:
```kotlin
responseFields(
    fieldsWithBasic(
        "data.id" type NUMBER means "고유 ID",
        "data.name" type STRING means "이름",
        "data.email" type STRING means "이메일 (선택)" isOptional true,
        "data.createdAt" type STRING means "생성일시" isOptional true
    )
)
```

### Path Variables 문서화

```kotlin
pathParameters(
    "id" pathMeans "도메인 ID"
)
```

### Query Parameters 문서화

```kotlin
queryParameters(
    "limit" queryMeans "조회 개수" isOptional true,
    "offset" queryMeans "조회 시작점" isOptional true
)
```

### 헬퍼 메서드 활용

`ControllerTestContext`에서 제공하는 헬퍼 메서드들을 사용하여 더 간결한 코드 작성이 가능합니다:

#### 파라미터 헬퍼 메서드

```kotlin
// Path Parameter
"id" pathMeans "도메인 ID"

// Query Parameter  
"limit" queryMeans "조회 개수"

// Optional 설정
"limit" queryMeans "조회 개수" isOptional true
```

#### 필드 헬퍼 메서드

```kotlin
// 기본 필드 (필수)
"name" type STRING means "도메인명"

// Optional 필드 (선택적)
"description" type STRING means "설명 (선택)" isOptional true

// Enum 필드 (가능한 값들 자동 표시)
"gender" type STRING means "성별" withEnum User.Gender::class
"status" type STRING means "상태 (선택)" withEnum Order.Status::class isOptional true

// 다양한 타입별 예시
"id" type NUMBER means "고유 ID",
"isActive" type BOOLEAN means "활성 상태",
"tags" type ARRAY means "태그 목록" isOptional true,
"metadata" type OBJECT means "메타데이터" isOptional true,
"createdAt" type STRING means "생성일시",
"updatedAt" type STRING means "수정일시" isOptional true
```

#### withEnum 사용 가이드라인

**목적**: Enum 타입 필드에 대해 가능한 모든 상수값들을 자동으로 문서에 포함시켜 API 사용자가 유효한 값을 쉽게 확인할 수 있도록 함.

**동작 방식**: 
- Enum 클래스의 모든 상수를 추출하여 필드 설명에 자동 추가
- 형식: `"원래 설명 (ENUM_VALUE1, ENUM_VALUE2, ENUM_VALUE3)"`

**기본 문법**:
```kotlin
// 필수 enum 필드
"gender" type STRING means "성별" withEnum User.Gender::class

// 선택적 enum 필드 (isOptional과 조합)
"activityLevel" type STRING means "활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true

// responseFields에서도 동일하게 사용
"data.status" type STRING means "주문 상태" withEnum Order.Status::class
```

**실제 프로젝트 사용 예시**:
```kotlin
// 사용자 생성 API - 성별 필드 (필수)
requestFields(
    "name" type STRING means "사용자 이름",
    "gender" type STRING means "사용자 성별" withEnum User.Gender::class,
    "age" type NUMBER means "사용자 나이"
)

// 사용자 수정 API - 활동 수준 필드 (선택)
requestFields(
    "name" type STRING means "사용자 이름 (선택)" isOptional true,
    "activityLevel" type STRING means "사용자 활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true
)

// 응답 필드에서의 사용
responseFields(
    fieldsWithBasic(
        "data.id" type NUMBER means "사용자 ID",
        "data.gender" type STRING means "사용자 성별" withEnum User.Gender::class,
        "data.activityLevel" type STRING means "사용자 활동 수준" withEnum User.ActivityLevel::class
    )
)
```

**자동 생성되는 문서화 예시**:
- `User.Gender`: "사용자 성별 (MALE, FEMALE, OTHER)"
- `User.ActivityLevel`: "사용자 활동 수준 (SEDENTARY, LIGHT, MODERATE, ACTIVE)"

**적용 대상**:
- 모든 Enum 타입 필드 (requestFields, responseFields, pathParameters, queryParameters)
- 필수 필드와 선택적 필드 모두 적용 가능
- 기존 `isOptional`과 함께 조합하여 사용

#### isOptional 사용 가이드라인

**1. Payload 클래스 분석**:
```kotlin
// Payload에서 nullable 필드는 isOptional true
data class CreateRequest(
    val name: String,           // 필수 → isOptional 생략
    val email: String? = null   // 선택 → isOptional true
)
```

**2. 문서화 일관성**:
- 필드명 뒤에 "(선택)" 표시와 `isOptional true` 같이 사용
- 필수 필드는 둘 다 생략

**3. 실제 사용 예시**:
```kotlin
requestFields(
    "uuid" type STRING means "사용자 UUID",
    "name" type STRING means "사용자 이름", 
    "email" type STRING means "이메일 (선택)" isOptional true,
    "age" type NUMBER means "나이",
    "phone" type STRING means "연락처 (선택)" isOptional true
)
```

**프로젝트 실제 사용 예시**:
```kotlin
// 사용자 생성 API (withEnum 적용)
requestFields(
    "uuid" type STRING means "사용자 UUID",
    "name" type STRING means "사용자 이름",
    "gender" type STRING means "사용자 성별" withEnum User.Gender::class,
    "age" type NUMBER means "사용자 나이",
    "height" type NUMBER means "사용자 키 (cm 단위)",
    "weight" type NUMBER means "사용자 몸무게 (kg 단위)",
    "email" type STRING means "사용자 이메일 (선택)" isOptional true,
    "activityLevel" type STRING means "사용자 활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true,
    "smi" type NUMBER means "사용자 SMI (선택)" isOptional true,
    "fatPercentage" type NUMBER means "사용자 체지방률 (선택)" isOptional true
)

// 음식 생성 API  
requestFields(
    "uuid" type STRING means "음식 UUID",
    "name" type STRING means "음식명",
    "servingSize" type NUMBER means "1회 기준 섭취량",
    "unit" type STRING means "단위 (예: g, ml 등)",
    "carbohydrates" type NUMBER means "탄수화물 함량 (g, 선택)" isOptional true,
    "protein" type NUMBER means "단백질 함량 (g, 선택)" isOptional true,
    "fat" type NUMBER means "지방 함량 (g, 선택)" isOptional true
)

// 어드민 사용자 수정 API (모든 필드 선택적, withEnum 적용)
requestFields(
    "name" type STRING means "이름 (선택)" isOptional true,
    "email" type STRING means "이메일 (선택)" isOptional true,
    "gender" type STRING means "성별 (선택)" withEnum User.Gender::class isOptional true,
    "age" type NUMBER means "나이 (선택)" isOptional true,
    "height" type NUMBER means "키 (cm) (선택)" isOptional true,
    "weight" type NUMBER means "몸무게 (kg) (선택)" isOptional true,
    "activityLevel" type STRING means "활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true
)
```

## Mock 설정

### 📋 Mock 설정 원칙

1. **컨트롤러의 모든 의존성을 Mock으로 설정**
2. **각 테스트 메서드에서 필요한 Mock만 every로 정의**
3. **실제 메서드 호출에 맞는 정확한 Mock 설정**

### 컨트롤러 의존성 Mock 설정

```kotlin
@WebMvcTest(FoodV1Controller::class)
class FoodV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    // 컨트롤러에 주입되는 모든 의존성을 MockkBean으로 설정
    @MockkBean(relaxed = true)
    private lateinit var foodService: FoodService
    
    // 추가 의존성이 있다면 모두 Mock 처리
    @MockkBean(relaxed = true) 
    private lateinit var someOtherService: SomeOtherService
}
```

### Every 절 사용 원칙

```kotlin
@Test
fun success() {
    // ✅ 올바른 접근: 해당 테스트에서 실제 호출되는 메서드만 Mock
    val request = mockCreateRequest()
    every { foodService.create(any(), any()) } returns mockFoodDto()
    // foodService.create()만 Mock 설정 (다른 메서드는 설정하지 않음)

    given()
        .body(request)
        .post("/v1/foods")
        .then()
        .log().all()
        .apply(documentationHandler())
        .status(HttpStatus.CREATED)
}

@Test 
fun getDetailsSuccess() {
    // ✅ 이 테스트에서는 getDetails()만 Mock
    every { foodService.getDetails(any()) } returns mockFoodDto()
    // foodService.create()는 설정하지 않음 (사용되지 않기 때문)

    given()
        .get("/v1/foods/1")
        .then()
        .log().all()
        .apply(documentationHandler())
        .status(HttpStatus.OK)
}
```

### ❌ 잘못된 Mock 사용 예시

```kotlin
// ❌ 잘못된 접근: 사용하지 않는 메서드까지 Mock
@Test
fun success() {
    val request = mockCreateRequest()
    every { foodService.create(any(), any()) } returns mockFoodDto()
    every { foodService.getDetails(any()) } returns mockFoodDto()  // 불필요한 Mock
    every { foodService.update(any(), any(), any()) } returns mockFoodDto()  // 불필요한 Mock
    
    // 실제로는 create()만 호출됨
}
```

### Mock 데이터 준비

```kotlin
private fun mockDomainDto(): DomainDto {
    return DomainDto(
        id = 1L,
        uuid = "test-uuid-123",
        name = "테스트 도메인",
        userId = 1L,
        createdAt = LocalDateTime.now()
    )
}

private fun mockCreateRequest(): DomainV1Request.Create {
    return DomainV1Request.Create(
        uuid = "test-uuid-456",
        name = "새로운 도메인",
        description = "테스트 설명"
    )
}
```

### 매개변수 매칭

```kotlin
// ✅ 무조건 모든 파라미터에 any() 사용
every { foodService.create(any(), any()) } returns mockFoodDto()
```

## Request/Response 테스트

### POST 요청 테스트

```kotlin
given()
    .body(request)
    .post("/v1/domain")
    .then()
    .log().all()
    .apply(documentationHandler())
    .status(HttpStatus.CREATED)
```

### GET 요청 테스트

```kotlin
given()
    .get("/v1/domain/1")
    .then()
    .log().all()
    .apply(documentationHandler())
    .status(HttpStatus.OK)
```

### PUT 요청 테스트

```kotlin
given()
    .body(request)
    .put("/v1/domain/1")
    .then()
    .log().all()
    .apply(documentationHandler())
    .status(HttpStatus.OK)
```

### Query Parameter가 있는 GET 요청

```kotlin
given()
    .params("limit", 10)
    .params("offset", 0)
    .get("/v1/domain")
    .then()
    .log().all()
    .apply(documentationHandler())
    .status(HttpStatus.OK)
```

**필요한 import 추가:**
```kotlin
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
```

## Best Practices

### 1. 테스트 독립성 보장
```kotlin
// 각 테스트는 독립적으로 실행 가능해야 함
// static 데이터나 공유 상태 사용 금지
// 성공 케이스만 테스트하므로 단순하고 명확한 구조 유지
```

### 2. 현실적인 테스트 데이터
```kotlin
// 한국어 이름과 현실적인 값 사용
private fun mockFoodDto(): FoodDto {
    return FoodDto(
        name = "백미밥",
        servingSize = 210.0,
        unit = "g",
        carbohydrates = 77.0,
        protein = 7.4,
        fat = 0.6
    )
}
```

### 3. 일관된 응답 구조 검증
```kotlin
responseFields(
    fieldsWithBasic( // 기본 응답 필드 (status, serverDatetime, message) 포함
        "data.field1" type TYPE means "필드 설명",
        "data.field2" type TYPE means "필드 설명 (선택)" isOptional true
    )
)
```

### 4. isOptional 사용 일관성 유지
```kotlin
// ✅ 올바른 접근: 필수/선택 필드 명확히 구분
requestFields(
    "name" type STRING means "이름", // 필수 필드
    "email" type STRING means "이메일 (선택)" isOptional true // 선택 필드
)

// ❌ 잘못된 접근: isOptional과 설명이 불일치
requestFields(
    "name" type STRING means "이름 (선택)", // 설명은 선택이라 했는데
    "email" type STRING means "이메일" isOptional true // isOptional 표시 없음
)
```

### 5. withEnum 사용 일관성 유지
```kotlin
// ✅ 올바른 접근: 모든 Enum 필드에 withEnum 적용
requestFields(
    "gender" type STRING means "성별" withEnum User.Gender::class,
    "status" type STRING means "상태 (선택)" withEnum Order.Status::class isOptional true
)

// ❌ 잘못된 접근: Enum 필드인데 withEnum 누락
requestFields(
    "gender" type STRING means "성별", // Enum 필드인데 withEnum 없음
    "status" type STRING means "상태"  // 유효한 값들을 확인하기 어려움
)
```

### 4. 로깅 활용
```kotlin
.then()
.log().all() // 요청/응답 로그 출력 (디버깅용)
```

### 5. 헬퍼 메서드 활용
```kotlin
private fun documentationHandler(testName: String, operation: String) = document(
    identifier(testName),
    ResourceSnippetParametersBuilder()
        .tag(Tags.FOOD.tagName)
        .description(Tags.FOOD.descriptionWith(operation)),
    // 응답 필드 설정...
)

// 사용 예시
.apply(documentationHandler("CreateTest", "생성"))
.apply(documentationHandler("GetDetailsTest", "상세 조회"))
```

### 6. 성공 케이스 중심 테스트
```kotlin
// ✅ 올바른 접근: 성공 케이스만 테스트
@Test
fun success() {
    // 정상적인 요청과 응답만 검증
}

// ❌ 잘못된 접근: 오류 케이스 테스트 금지
@Test
fun `should throw exception when invalid request`() {
    // 이런 테스트는 작성하지 않음
}
```


## 테스트 실행

### 개별 테스트 실행
```bash
./gradlew :application:balance-eat-api:test --tests "*DomainV1ControllerTest*"
```

### 특정 테스트 메서드 실행
```bash
./gradlew :application:balance-eat-api:test --tests "*DomainV1ControllerTest.CreateTest.success"
```

### 테스트 보고서 확인
테스트 실행 후 `application/balance-eat-api/build/reports/tests/test/index.html`에서 상세 보고서 확인 가능

## 참고사항

### Tags 열거형
`ControllerTestContext`에서 제공하는 `Tags` 열거형 활용:
```kotlin
Tags.FOOD.tagName    // "음식"
Tags.USER.tagName    // "사용자" (추가 시)
```

### 기본 응답 필드
`fieldsWithBasic()`으로 기본 응답 구조 자동 포함:
- `status`: 결과 상태
- `serverDatetime`: 응답 시간
- `message`: 메시지

### 코드 품질
- 세미콜론(;) 사용 금지 (Kotlin 관례)
- 일관된 들여쓰기 유지
- 의미있는 테스트 메서드명 사용