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
5. [Mock 설정](#mock-설정)
6. [Request/Response 테스트](#requestresponse-테스트)
7. [Best Practices](#best-practices)

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
    "description" type STRING means "설명 (선택사항)"
)
```

### Path Variables 문서화

```kotlin
pathParameters(
    parameterWithName("id").description("도메인 ID")
)
```

### Query Parameters 문서화

```kotlin
requestParameters(
    parameterWithName("limit").description("조회 개수").optional(),
    parameterWithName("offset").description("조회 시작점").optional()
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
        perCapitaIntake = 210.0,
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
        "data.field2" type TYPE means "필드 설명"
    )
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