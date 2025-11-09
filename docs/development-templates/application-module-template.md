# Balance-Eat Application Layer 표준 템플릿

## 필수 규칙

- **주석 금지**: 코드 내 주석은 작성하지 않습니다
- **검증 로직**: Controller에서는 `@Valid`를 통한 Bean Validation만 수행하고, 비즈니스 검증은 Service 레이어에서 처리합니다
- **트랜잭션 관리**: Service 레이어에서 `@Transactional` 적용, Controller에서는 트랜잭션 처리하지 않습니다

## 목적

Balance-Eat 프로젝트의 Application 레이어 표준 템플릿을 제공합니다. 각 도메인은 Controller, Service, Payload(Request/Response) 구조를 가지며, Kotlin + Spring Boot + REST Docs를 기반으로 합니다.

---

## 표준 패키지 구조

```
application/balance-eat-api/src/main/kotlin/org/balanceeat/api/{domain}/
├── {Domain}V1Controller.kt          # REST API 컨트롤러
├── {Domain}Service.kt                # Application 서비스
└── {Domain}V1Payload.kt              # Request/Response 객체

application/balance-eat-api/src/testFixtures/kotlin/org/balanceeat/api/{domain}/
└── {Domain}V1PayloadFixture.kt       # Request/Response 픽스처

application/balance-eat-api/src/test/kotlin/org/balanceeat/api/{domain}/
├── {Domain}V1ControllerTest.kt       # 컨트롤러 테스트 (REST Docs)
└── {Domain}ServiceTest.kt            # 서비스 테스트
```

**파일 설명**:
- **{Domain}V1Controller.kt**: REST API 엔드포인트 정의, 버전 명시 (V1)
- **{Domain}Service.kt**: Application 비즈니스 로직, Domain Service 호출
- **{Domain}V1Payload.kt**: Request와 Response 객체 (API 버전별 분리)
- **{Domain}V1PayloadFixture.kt**: Request와 Response 객체 테스트 픽스처
- **{Domain}V1ControllerTest.kt**: Controller 테스트 + REST Docs 생성
- **{Domain}ServiceTest.kt**: Service 레이어 통합 테스트

---

## 템플릿 1: Controller ({Domain}V1Controller.kt)

```kotlin
package org.balanceeat.api.food

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.domain.food.FoodSearchResult
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/foods")
class FoodV1Controller(
    private val foodService: FoodService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody @Valid request: FoodV1Request.Create
    ): ApiResponse<FoodV1Response.Details> {
        val result = foodService.create(request, 1L)
        return ApiResponse.success(result)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: FoodV1Request.Update
    ): ApiResponse<FoodV1Response.Details> {
        val result = foodService.update(request, id, 1L)
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getDetails(
        @PathVariable id: Long
    ): ApiResponse<FoodV1Response.Details> {
        val result = foodService.getDetails(id)
        return ApiResponse.success(result)
    }

    @GetMapping("/recommendations")
    fun getRecommendations(
        @RequestParam(defaultValue = "10") limit: Int
    ): ApiResponse<List<FoodV1Response.Details>> {
        val result = foodService.getRecommendations(limit)
        return ApiResponse.success(result)
    }

    @GetMapping("/search")
    fun search(
        request: FoodV1Request.Search,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<FoodSearchResult>> {
        val result = foodService.search(request, pageable)

        return ApiResponse.success(result)
    }
}
```

**핵심 패턴**:
- `@RestController` + `@RequestMapping("/v1/{domain}s")` 구조
- 생성자 주입으로 Service 의존성 관리
- `@Valid`를 통한 Bean Validation 적용
- `ApiResponse<T>` 래퍼로 통일된 응답 형식
- HTTP 상태 코드 명시 (`@ResponseStatus`, 기본값 200 OK)
- `@PathVariable`, `@RequestBody`, `@RequestParam` 적절히 활용
- Pageable은 `@PageableDefault`로 기본값 제공
- **Service 결과를 변환 없이 반환**: Service가 이미 Response를 반환하므로 Controller에서는 변환 불필요

---

## 템플릿 2: Service ({Domain}Service.kt)

```kotlin
package org.balanceeat.api.food

import org.balanceeat.apibase.ApplicationStatus.CANNOT_MODIFY_FOOD
import org.balanceeat.apibase.ApplicationStatus.FOOD_NOT_FOUND
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.domain.food.FoodCommand
import org.balanceeat.domain.food.FoodDomainService
import org.balanceeat.domain.food.FoodDto
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.food.FoodSearchResult
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodService(
    private val foodDomainService: FoodDomainService,
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getDetails(id: Long): FoodV1Response.Details {
        val food = foodRepository.findByIdOrNull(id)
            ?.let { FoodDto.from(it) }
            ?: throw NotFoundException(FOOD_NOT_FOUND)

        return FoodV1Response.Details.from(food)
    }

    @Transactional
    fun create(request: FoodV1Request.Create, creatorId: Long): FoodV1Response.Details {
        val result = foodDomainService.create(
            command = FoodCommand.Create(
                uuid = request.uuid,
                userId = creatorId,
                name = request.name,
                servingSize = request.servingSize,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                brand = request.brand,
                isAdminApproved = false
            )
        )

        return FoodV1Response.Details.from(result)
    }

    @Transactional
    fun update(request: FoodV1Request.Update, id: Long, modifierId: Long): FoodV1Response.Details {
        val food = foodRepository.findByIdOrNull(id)
            ?: throw NotFoundException(FOOD_NOT_FOUND)

        if (food.userId != modifierId) {
            throw BadRequestException(CANNOT_MODIFY_FOOD)
        }

        val result = foodDomainService.update(
            command = FoodCommand.Update(
                id = id,
                name = request.name,
                servingSize = request.servingSize,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                brand = request.brand,
                isAdminApproved = false
            )
        )

        return FoodV1Response.Details.from(result)
    }

    @Transactional(readOnly = true)
    fun getRecommendations(limit: Int = 10): List<FoodV1Response.Details> {
        val pageable = PageRequest.of(0, limit)
        val result = foodRepository.findRecommendedFoods(pageable)

        return result.map { FoodV1Response.Details.from(FoodDto.from(it)) }
    }

    @Transactional(readOnly = true)
    fun search(request: FoodV1Request.Search, pageable: Pageable): PageResponse<FoodSearchResult> {
        val result = foodRepository.search(
            FoodCommand.Search(
                foodName = request.foodName,
                userId = request.userId,
                pageable = pageable
            )
        )

        return PageResponse.from(result)
    }
}
```

**핵심 패턴**:
- `@Service` 어노테이션
- `@Transactional(readOnly = true)` 클래스 레벨 적용 권장
- 쓰기 작업에만 `@Transactional` 오버라이드
- Request → Command 변환 로직
- Domain Service 호출로 비즈니스 로직 위임
- Application 레이어 검증 (권한, 존재 여부 등)
- `findByIdOrNull()` + Elvis 연산자로 예외 처리
- **Domain Dto → Response 변환**: Service 레이어에서 Response까지 변환하여 반환

---

## 템플릿 3: Payload ({Domain}V1Payload.kt)

```kotlin
package org.balanceeat.api.food

import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.food.FoodDto
import java.time.LocalDateTime

class FoodV1Request {
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        val uuid: String,

        @field:NotNull(message = "name은 필수입니다")
        val name: String,

        @field:NotNull(message = "servingSize는 필수입니다")
        val servingSize: Double,

        @field:NotNull(message = "unit은 필수입니다")
        val unit: String,

        @field:NotNull(message = "carbohydrates는 필수입니다")
        val carbohydrates: Double,

        @field:NotNull(message = "protein은 필수입니다")
        val protein: Double,

        @field:NotNull(message = "fat는 필수입니다")
        val fat: Double,

        @field:NotNull(message = "perServingCalories는 필수입니다")
        val perServingCalories: Double,

        @field:NotNull(message = "brand는 필수입니다")
        val brand: String
    )

    data class Update(
        @field:NotNull(message = "음식명은 필수입니다")
        val name: String,

        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        val servingSize: Double,

        @field:NotNull(message = "단위는 필수입니다")
        val unit: String,

        @field:NotNull(message = "탄수화물 함량은 필수입니다")
        val carbohydrates: Double,

        @field:NotNull(message = "단백질 함량은 필수입니다")
        val protein: Double,

        @field:NotNull(message = "지방 함량은 필수입니다")
        val fat: Double,

        @field:NotNull(message = "1회 제공량 기준 칼로리는 필수입니다")
        val perServingCalories: Double,

        @field:NotNull(message = "브랜드는 필수입니다")
        val brand: String
    )

    data class Search(
        val foodName: String?,
        val userId: Long?,
    )
}

class FoodV1Response {
    data class Details(
        val id: Long,
        val uuid: String,
        val name: String,
        val servingSize: Double,
        val unit: String,
        val perServingCalories: Double,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val brand: String? = null,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun from(food: FoodDto) = Details(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                servingSize = food.servingSize,
                unit = food.unit,
                perServingCalories = food.perServingCalories,
                carbohydrates = food.carbohydrates,
                protein = food.protein,
                fat = food.fat,
                brand = food.brand,
                createdAt = food.createdAt
            )
        }
    }
}
```

**핵심 패턴**:
- Request/Response를 별도 클래스로 분리
- Request 내부에 Create, Update, Search 등 중첩 클래스 정의
- Response 내부에 Details, Summary 등 중첩 클래스 정의
- `@field:NotNull` + message로 Bean Validation 적용 (String의 경우 @field:NotBlank)
- 선택 필드는 nullable(`?`) 타입으로 선언
- companion object의 `from()` 팩토리 메서드로 Domain Dto 변환

---

## 템플릿 4: Request Fixture ({Domain}V1RequestFixture.kt)

```kotlin
package org.balanceeat.api.food

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.common.utils.CalorieCalculator
import java.util.UUID

class FoodV1RequestFixture {
    data class Create(
        val uuid: String = UUID.randomUUID().toString(),
        val name: String = "테스트 음식",
        val servingSize: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0,
        val perServingCalories: Double = CalorieCalculator.calculate(carbohydrates, protein, fat),
        val brand: String = "테스트 브랜드"
    ): TestFixture<FoodV1Request.Create> {
        override fun create(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = uuid,
                name = name,
                servingSize = servingSize,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                perServingCalories = perServingCalories,
                brand = brand
            )
        }
    }

    data class Update(
        val name: String = "테스트 음식",
        val servingSize: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0,
        val perServingCalories: Double = 159.0,
        val brand: String = "테스트 브랜드"
    ): TestFixture<FoodV1Request.Update> {
        override fun create(): FoodV1Request.Update {
            return FoodV1Request.Update(
                name = name,
                servingSize = servingSize,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                perServingCalories = perServingCalories,
                brand = brand
            )
        }
    }

    data class Search(
        val foodName: String? = null,
        val userId: Long? = null
    ): TestFixture<FoodV1Request.Search> {
        override fun create(): FoodV1Request.Search {
            return FoodV1Request.Search(
                foodName = foodName,
                userId = userId
            )
        }
    }
}

fun foodCreateV1RequestFixture(block: FoodV1RequestFixture.Create.() -> Unit = {}): FoodV1Request.Create {
    return FoodV1RequestFixture.Create().copy().apply(block).create()
}

fun foodUpdateV1RequestFixture(block: FoodV1RequestFixture.Update.() -> Unit = {}): FoodV1Request.Update {
    return FoodV1RequestFixture.Update().copy().apply(block).create()
}

fun foodSearchV1RequestFixture(block: FoodV1RequestFixture.Search.() -> Unit = {}): FoodV1Request.Search {
    return FoodV1RequestFixture.Search().copy().apply(block).create()
}
```

**핵심 패턴**:
- Request별 Fixture data class 정의
- `TestFixture<T>` 인터페이스 구현
- 기본값 제공으로 간편한 테스트 작성
- data class의 `copy()` 메서드 활용
- 최상위 함수로 DSL 스타일 픽스처 생성 함수 제공
- 함수명 패턴: `{domain}{Operation}V1RequestFixture`

---

## 템플릿 5: Controller Test ({Domain}V1ControllerTest.kt)

Controller 테스트는 REST API 엔드포인트를 검증하고 REST Docs를 자동 생성합니다.

```kotlin
package org.balanceeat.api.food

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.food.FoodDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import java.time.LocalDateTime

@WebMvcTest(FoodV1Controller::class)
class FoodV1ControllerTest: ControllerTestContext() {
    @MockkBean(relaxed = true)
    private lateinit var foodService: FoodService

    @Nested
    @DisplayName("POST /v1/foods - 음식 생성")
    inner class CreateTest {
        @Test
        fun success() {
            val request = mockCreateRequest()
            every { foodService.create(any(), any()) } returns mockFoodResponse()

            given()
                .body(request)
                .post("/v1/foods")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("CreateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.FOOD.tagName)
                            .description(Tags.FOOD.descriptionWith("생성")),
                        requestFields(
                            "uuid" type STRING means "음식 UUID",
                            "name" type STRING means "음식명",
                            "servingSize" type NUMBER means "1회 기준 섭취량",
                            "unit" type STRING means "단위",
                            "carbohydrates" type NUMBER means "탄수화물 함량",
                            "protein" type NUMBER means "단백질 함량",
                            "fat" type NUMBER means "지방 함량",
                            "perServingCalories" type NUMBER means "칼로리",
                            "brand" type STRING means "브랜드명"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.name" type STRING means "음식명",
                                "data.servingSize" type NUMBER means "1회 제공량",
                                "data.unit" type STRING means "단위",
                                "data.perServingCalories" type NUMBER means "칼로리",
                                "data.carbohydrates" type NUMBER means "탄수화물",
                                "data.protein" type NUMBER means "단백질",
                                "data.fat" type NUMBER means "지방",
                                "data.brand" type STRING means "브랜드명",
                                "data.createdAt" type STRING means "생성일시",
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED)
        }

        private fun mockCreateRequest(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = "test-uuid-456",
                name = "새로운 음식",
                servingSize = 150.0,
                unit = "ml",
                carbohydrates = 30.0,
                protein = 10.0,
                fat = 5.0,
                perServingCalories = 185.0,
                brand = "테스트 브랜드"
            )
        }
    }

    @Nested
    @DisplayName("PUT /v1/foods/{id} - 음식 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            val request = mockUpdateRequest()
            every { foodService.update(any(), any(), any()) } returns mockFoodResponse()

            given()
                .body(request)
                .put("/v1/foods/{id}", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.FOOD.tagName)
                            .description(Tags.FOOD.descriptionWith("수정")),
                        pathParameters(
                            "id" pathMeans "음식 ID"
                        ),
                        requestFields(
                            "name" type STRING means "음식명",
                            "servingSize" type NUMBER means "1회 기준 섭취량",
                            "unit" type STRING means "단위",
                            "carbohydrates" type NUMBER means "탄수화물 함량",
                            "protein" type NUMBER means "단백질 함량",
                            "fat" type NUMBER means "지방 함량",
                            "perServingCalories" type NUMBER means "칼로리",
                            "brand" type STRING means "브랜드명"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.name" type STRING means "음식명",
                                "data.servingSize" type NUMBER means "1회 제공량",
                                "data.unit" type STRING means "단위",
                                "data.perServingCalories" type NUMBER means "칼로리",
                                "data.carbohydrates" type NUMBER means "탄수화물",
                                "data.protein" type NUMBER means "단백질",
                                "data.fat" type NUMBER means "지방",
                                "data.brand" type STRING means "브랜드명",
                                "data.createdAt" type STRING means "생성일시",
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }

        private fun mockUpdateRequest(): FoodV1Request.Update {
            return FoodV1Request.Update(
                name = "수정된 음식",
                servingSize = 200.0,
                unit = "g",
                carbohydrates = 40.0,
                protein = 15.0,
                fat = 8.0,
                perServingCalories = 292.0,
                brand = "수정된 브랜드"
            )
        }
    }

    private fun mockFoodResponse(): FoodV1Response.Details {
        return FoodV1Response.Details(
            id = 1L,
            uuid = "test-uuid-123",
            name = "테스트 음식",
            servingSize = 100.0,
            unit = "g",
            perServingCalories = 150.0,
            carbohydrates = 25.0,
            protein = 8.0,
            fat = 3.0,
            brand = "테스트 브랜드",
            createdAt = LocalDateTime.now()
        )
    }
}
```

**핵심 패턴**:
- **@WebMvcTest**: 특정 Controller만 로드하는 경량 테스트
- **@MockkBean**: Service 레이어 Mocking
- **ControllerTestContext**: REST Docs 설정이 포함된 베이스 클래스
- **@Nested + inner class**: 엔드포인트별 테스트 그룹화
- **given().post/get/put/delete().then()**: REST Assured DSL 스타일
- **document()**: REST Docs 생성 (identifier, tag, description, fields)
- **requestFields / responseFields**: API 문서 필드 정의
- **pathParameters**: 경로 변수 문서화

---

## 템플릿 6: Service Test ({Domain}ServiceTest.kt)

Service 테스트는 Application 레이어의 비즈니스 로직을 검증합니다.

```kotlin
package org.balanceeat.api.food

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.domain.food.FoodFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FoodServiceTest: IntegrationTestContext() {
    @Autowired
    private lateinit var foodService: FoodService

    @Nested
    @DisplayName("음식 상세 조회")
    inner class GetDetailsTest {
        @Test
        fun `음식 ID로 음식 상세 정보를 조회할 수 있다`() {
            val food = createEntity(FoodFixture(name = "테스트 음식").create())

            val result = foodService.getDetails(food.id)

            assertThat(result).usingRecursiveComparison()
                .isEqualTo(food)
        }
    }

    @Nested
    @DisplayName("음식 생성")
    inner class CreateTest {
        @Test
        fun `음식을 성공적으로 생성할 수 있다`() {
            val creatorId = 100L
            val request = FoodV1RequestFixture.Create(
                name = "닭가슴살",
                brand = "CJ 제일제당"
            ).create()

            val result = foodService.create(request, creatorId)

            assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "isAdminApproved", "userId")
                .isEqualTo(request)
        }
    }

    @Nested
    @DisplayName("음식 수정")
    inner class UpdateTest {
        @Test
        fun `음식 생성자와 수정자가 다르면 실패한다`() {
            val creatorId = 1L
            val modifierId = 2L
            val food = createEntity(FoodFixture(userId = creatorId).create())
            val request = FoodV1RequestFixture.Update().create()

            val throwable = catchThrowable {
                foodService.update(
                    request = request,
                    id = food.id,
                    modifierId = modifierId
                )
            }

            assertThat(throwable).isInstanceOf(BadRequestException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.CANNOT_MODIFY_FOOD)
        }

        @Test
        fun `음식 생성자와 수정자가 같으면 수정 성공한다`() {
            val userId = 1L
            val food = createEntity(FoodFixture(userId = userId, name = "기존 음식").create())
            val request = FoodV1RequestFixture.Update(
                name = "수정된 음식",
                brand = "수정된 브랜드"
            ).create()

            val result = foodService.update(
                request = request,
                id = food.id,
                modifierId = userId
            )

            assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "uuid", "createdAt", "isAdminApproved", "userId")
                .isEqualTo(request)
        }
    }
}
```

**핵심 패턴**:
- **IntegrationTestContext**: 통합 테스트 베이스 클래스 (DB, Transaction 포함)
- **@Nested + inner class**: 기능별 테스트 그룹화
- **Given-When-Then**: 명확한 테스트 구조
- **AssertJ assertions**: `assertThat()`, `usingRecursiveComparison()`, `catchThrowable()`
- **createEntity()**: 베이스 클래스의 엔티티 생성 헬퍼 메서드
- **Fixture 활용**: Request Fixture로 테스트 데이터 생성
- **비즈니스 로직 검증**: 권한 체크, 데이터 무결성 등

**작성 가이드**:
1. **성공 케이스 우선**: 각 기능의 정상 동작을 먼저 테스트
2. **Application 레이어 검증**: 권한 체크, 존재 여부, 데이터 변환 등
3. **예외 케이스**: Application 레이어 예외 상황 검증
4. **Domain 로직 제외**: Domain Service의 비즈니스 로직은 Domain 레이어에서 테스트

---

## 개발 체크리스트

### 새로운 API 추가시

- [ ] **Controller**: `{Domain}V1Controller.kt` 작성, HTTP 메서드 및 경로 정의
- [ ] **Service**: `{Domain}Service.kt` 작성, `@Transactional` 적용
- [ ] **Payload**: `{Domain}V1Payload.kt` 작성, Request/Response 정의
- [ ] **Validation**: Request에 `@field:NotNull` 등 Bean Validation 적용
- [ ] **Request Fixture**: `{Domain}V1RequestFixture.kt` 작성
- [ ] **Controller Test**: `{Domain}V1ControllerTest.kt` 작성, REST Docs 생성
- [ ] **Service Test**: `{Domain}ServiceTest.kt` 작성
- [ ] **예외 처리**: `ApplicationStatus`에 Application 레이어 예외 상태 추가

### 코드 품질 체크

- [ ] 주석 없이 코드 작성
- [ ] Controller에서는 검증 로직 최소화 (Bean Validation만)
- [ ] Service 레이어에서 비즈니스 검증 수행
- [ ] 트랜잭션 범위 적절히 설정
- [ ] REST Docs 생성 및 API 문서화
- [ ] 한글 에러 메시지 작성
- [ ] 테스트 커버리지 확인

---

## 명명 규칙 요약

| 항목 | 규칙 | 예시 |
|-----|------|------|
| Controller | `{Domain}V1Controller.kt` | `FoodV1Controller.kt` |
| Service | `{Domain}Service.kt` | `FoodService.kt` |
| Payload | `{Domain}V1Payload.kt` | `FoodV1Payload.kt` |
| Request | `{Domain}V1Request.{Operation}` | `FoodV1Request.Create` |
| Response | `{Domain}V1Response.{Type}` | `FoodV1Response.Details` |
| Request Fixture | `{Domain}V1RequestFixture.kt` | `FoodV1RequestFixture.kt` |
| Controller Test | `{Domain}V1ControllerTest.kt` | `FoodV1ControllerTest.kt` |
| Service Test | `{Domain}ServiceTest.kt` | `FoodServiceTest.kt` |

---

## 참고 사항

### 레이어별 책임

**Controller 레이어**:
- HTTP 요청/응답 처리
- Bean Validation을 통한 기본 검증
- Service 호출 (변환 없이 결과 반환)
- HTTP 상태 코드 관리

**Service 레이어**:
- Request → Command 변환
- Application 레이어 검증 (권한, 존재 여부 등)
- Domain Service 호출
- **Domain Dto → Response 변환** (중요!)
- 트랜잭션 관리

**Domain 레이어**:
- 비즈니스 로직 처리
- 엔티티 상태 변경
- 도메인 규칙 검증

### REST Docs 통합

프로젝트는 Spring REST Docs를 사용하여 API 문서를 자동 생성합니다:
- Controller Test 실행 시 자동으로 문서 조각 생성
- `build/generated-snippets/` 디렉토리에 저장
- OpenAPI 3.0 스펙으로 변환 가능

### 실제 도메인 예시

프로젝트 내 참고 가능한 도메인:
- `Food`: 음식 도메인 (CRUD + 검색 + 추천)
- `User`: 사용자 도메인 (생성 + 조회 + 수정)
- `Diet`: 식단 도메인 (복잡한 비즈니스 로직)

이 템플릿을 따라 개발하면 일관성 있고 유지보수가 용이한 Application 레이어를 구축할 수 있습니다.
