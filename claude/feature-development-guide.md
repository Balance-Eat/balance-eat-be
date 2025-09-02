# Balance-Eat 기능 개발 가이드

## 개요

이 문서는 Balance-Eat 프로젝트에서 새로운 기능을 개발할 때 따라야 할 전체적인 개발 플로우와 테스트 명세를 제공합니다. Food 도메인을 예시로 하여 Controller → Service → Repository로 이어지는 완전한 개발 플로우를 설명합니다.

## 1. 개발 플로우 개요

### 1.1 아키텍처 구조
```
Controller (API 계층)
    ↓
Application Service (애플리케이션 계층)
    ↓  
Domain Service (도메인 계층)
    ↓
Repository (인프라 계층)
```

### 1.2 개발 순서
1. **Domain Entity** 설계 및 구현
2. **Command 객체** 정의
3. **DTO 클래스** 생성
4. **Repository 인터페이스** 정의
5. **Domain Service** 구현
6. **Application Service** 구현
7. **API 계층** (Controller, Payload) 구현
8. **Test Fixtures** 생성
9. **테스트 코드** 작성

## 2. Domain Layer 구현

### 2.1 Entity 구현 (Food.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/Food.kt`

```kotlin
@Entity
@Table(name = "food")
class Food(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    val uuid: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "per_capita_intake", nullable = false)
    var perCapitaIntake: Double,

    @Column(name = "unit", length = 20, nullable = false)
    var unit: String,

    @Column(name = "carbohydrates", nullable = false)
    var carbohydrates: Double = 0.0,

    @Column(name = "protein", nullable = false)
    var protein: Double = 0.0,

    @Column(name = "fat", nullable = false)
    var fat: Double = 0.0,

    @Column(name = "is_verified", nullable = false)
    var isAdminApproved: Boolean = false
) : BaseEntity() {
    override fun guard() {
        // 검증 로직
    }
}
```

**핵심 원칙**:
- `BaseEntity` 상속 필수
- 모든 필드에 적절한 제약사항 설정
- `guard()` 메서드에서 비즈니스 규칙 검증
- 상세 가이드라인: [entity-guidelines.md](entity-guidelines.md) 참조

### 2.2 Command 객체 구현 (FoodCommand.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodCommand.kt`

```kotlin
class FoodCommand {
    data class Create(
        val uuid: String,
        val userId: Long,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val isAdminApproved: Boolean = false
    )

    data class Update(
        val id: Long,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val isAdminApproved: Boolean = false
    )
}
```

### 2.3 DTO 구현 (FoodDto.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodDto.kt`

```kotlin
data class FoodDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val userId: Long,
    val perCapitaIntake: Double,
    val unit: String,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val isAdminApproved: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(food: Food): FoodDto {
            return FoodDto(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                userId = food.userId,
                perCapitaIntake = food.perCapitaIntake,
                unit = food.unit,
                carbohydrates = food.carbohydrates,
                protein = food.protein,
                fat = food.fat,
                isAdminApproved = food.isAdminApproved,
                createdAt = food.createdAt
            )
        }
    }
}
```

### 2.4 Repository 인터페이스 (FoodRepository.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodRepository.kt`

```kotlin
interface FoodRepository : JpaRepository<Food, Long> {
    fun findByUuid(uuid: String): Food?
    fun findByUserId(userId: Long): List<Food>
}
```

### 2.5 Domain Service 구현 (FoodDomainService.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodDomainService.kt`

```kotlin
@DomainService
class FoodDomainService(
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getFood(foodId: Long): FoodDto {
        val food = foodRepository.findById(foodId)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }
        return FoodDto.from(food)
    }
    
    @Transactional
    fun create(command: FoodCommand.Create): FoodDto {
        val food = Food(
            name = command.name,
            uuid = command.uuid,
            userId = command.userId,
            perCapitaIntake = command.perCapitaIntake,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat,
            isAdminApproved = command.isAdminApproved
        )
        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
}
```

**핵심 원칙**:
- `@DomainService` 어노테이션 필수
- 항상 DTO 반환 (엔티티 직접 반환 금지)
- 트랜잭션 범위 명시 (`@Transactional`)

## 3. Application Layer 구현

### 3.1 Application Service 구현 (FoodService.kt)

**위치**: `application/balance-eat-api/src/main/kotlin/org/balanceeat/api/food/FoodService.kt`

```kotlin
@Service
class FoodService(
    private val foodDomainService: FoodDomainService,
    private val foodRepository: FoodRepository
) {
    @Transactional(readOnly = true)
    fun getDetails(id: Long): FoodDto {
        return foodRepository.findByIdOrNull(id)
            ?.let { FoodDto.from(it) }
            ?: throw NotFoundException(FOOD_NOT_FOUND)
    }

    @Transactional
    fun create(request: FoodV1Request.Create, creatorId: Long): FoodDto {
        return foodDomainService.create(
            command = FoodCommand.Create(
                uuid = request.uuid,
                userId = creatorId,
                name = request.name,
                perCapitaIntake = request.perCapitaIntake,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                isAdminApproved = false
            )
        )
    }
}
```

**역할**:
- API Request를 Domain Command로 변환
- 권한 검증 및 비즈니스 로직 조율
- Domain Service 호출

### 3.2 API Payload 구현 (FoodV1Payload.kt)

**위치**: `application/balance-eat-api/src/main/kotlin/org/balanceeat/api/food/FoodV1Payload.kt`

```kotlin
class FoodV1Request {
    @Schema(name = "FoodCreateRequest", description = "음식 생성 요청")
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        val uuid: String,
        
        @field:NotNull(message = "음식명은 필수입니다")
        @field:Size(min = 1, max = 100, message = "음식명은 1자 이상 100자 이하여야 합니다")
        val name: String,
        
        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        @field:Positive(message = "1회 기준 섭취량은 0보다 큰 값이어야 합니다")
        val perCapitaIntake: Double,
        
        // ... 기타 필드들
    )
}

class FoodV1Response {
    @Schema(name = "FoodDetailsResponse", description = "음식 상세정보 응답")
    data class Info(
        val id: Long,
        val uuid: String,
        val name: String,
        // ... 기타 필드들
    ) {
        companion object {
            fun from(food: FoodDto) = Info(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                // ... 필드 매핑
            )
        }
    }
}
```

### 3.3 API Spec 인터페이스 (FoodV1ApiSpec.kt)

**위치**: `application/balance-eat-api/src/main/kotlin/org/balanceeat/api/food/FoodV1ApiSpec.kt`

```kotlin
@Tag(name = "Food", description = "음식 관리 API")
interface FoodV1ApiSpec {
    @Operation(summary = "음식 생성", description = "새로운 음식을 등록합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "음식 생성 성공"),
        ApiResponse(responseCode = "400", description = "잘못된 요청")
    ])
    fun create(@RequestBody request: FoodV1Request.Create): ApiResponse<FoodV1Response.Info>

    @Operation(summary = "음식 상세 조회", description = "음식의 상세 정보를 조회합니다")
    fun getDetails(@PathVariable id: Long): ApiResponse<FoodV1Response.Info>
}
```

### 3.4 Controller 구현 (FoodV1Controller.kt)

**위치**: `application/balance-eat-api/src/main/kotlin/org/balanceeat/api/food/FoodV1Controller.kt`

```kotlin
@RestController
@RequestMapping("/v1/foods")
class FoodV1Controller(
    private val foodService: FoodService,
) : FoodV1ApiSpec {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@RequestBody @Valid request: FoodV1Request.Create): ApiResponse<FoodV1Response.Info> {
        val result = foodService.create(request, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(
            FoodV1Response.Info.from(result)
        )
    }

    @GetMapping("/{id}")
    override fun getDetails(@PathVariable id: Long): ApiResponse<FoodV1Response.Info> {
        val food = foodService.getDetails(id)
        return ApiResponse.success(FoodV1Response.Info.from(food))
    }
}
```

## 4. 테스트 코드 명세

### 4.1 Test Fixtures

#### 4.1.1 Entity Fixture

**위치**: `domain/src/testFixtures/kotlin/org/balanceeat/domain/food/FoodFixture.kt`

```kotlin
class FoodFixture(
    var id: Long = NEW_ID,
    var name: String = "테스트 음식",
    var uuid: String = UUID.randomUUID().toString(),
    var userId: Long = 1L,
    var perCapitaIntake: Double = 100.0,
    var unit: String = "g",
    var carbohydrates: Double = 25.0,
    var protein: Double = 8.0,
    var fat: Double = 3.0,
    var isAdminApproved: Boolean = false
) : TestFixture<Food> {
    override fun create(): Food {
        return Food(
            id = id,
            name = name,
            uuid = uuid,
            userId = userId,
            perCapitaIntake = perCapitaIntake,
            unit = unit,
            carbohydrates = carbohydrates,
            protein = protein,
            fat = fat,
            isAdminApproved = isAdminApproved
        )
    }
}
```

#### 4.1.2 Command Fixture

**위치**: `domain/src/testFixtures/kotlin/org/balanceeat/domain/food/FoodCommandFixture.kt`

```kotlin
class FoodCommandFixture {
    class Create(
        var uuid: String = UUID.randomUUID().toString(),
        var userId: Long = 1L,
        var name: String = "테스트 음식",
        var perCapitaIntake: Double = 100.0,
        var unit: String = "g",
        var carbohydrates: Double = 25.0,
        var protein: Double = 8.0,
        var fat: Double = 3.0,
        var isAdminApproved: Boolean = false
    ) : TestFixture<FoodCommand.Create> {
        override fun create(): FoodCommand.Create {
            return FoodCommand.Create(
                uuid = uuid,
                userId = userId,
                name = name,
                perCapitaIntake = perCapitaIntake,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                isAdminApproved = isAdminApproved
            )
        }
    }
}
```

#### 4.1.3 API Request Fixture

**위치**: `application/balance-eat-api/src/testFixtures/kotlin/org/balanceeat/api/food/FoodV1RequestFixture.kt`

```kotlin
class FoodV1RequestFixture {
    data class Create(
        val uuid: String = UUID.randomUUID().toString(),
        val name: String = "테스트 음식",
        val perCapitaIntake: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0
    ): TestFixture<FoodV1Request.Create> {
        override fun create(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = uuid,
                name = name,
                perCapitaIntake = perCapitaIntake,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat
            )
        }
    }
}
```

### 4.2 테스트 계층별 명세

#### 4.2.1 Domain Service 테스트

**위치**: `domain/src/test/kotlin/org/balanceeat/domain/food/FoodDomainServiceTest.kt`

```kotlin
class FoodDomainServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var foodDomainService: FoodDomainService
    
    @Test
    fun `음식을 생성할 수 있다`() {
        // given
        val command = FoodCommandFixture.Create(
            name = "김치찌개",
            isAdminApproved = true
        ).create()
        
        // when
        val result = foodDomainService.create(command)
        
        // then
        assertThat(result.name).isEqualTo("김치찌개")
        assertThat(result.isAdminApproved).isTrue()
    }
}
```

**테스트 포커스**:
- 비즈니스 로직 검증
- 도메인 규칙 준수 확인
- 엔티티 생명주기 테스트

#### 4.2.2 Application Service 테스트

**위치**: `application/balance-eat-api/src/test/kotlin/org/balanceeat/api/food/FoodServiceTest.kt`

```kotlin
class FoodServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var foodService: FoodService
    
    @Test
    fun `권한이 없는 사용자가 음식을 수정하려 할 때 실패한다`() {
        // given
        val creatorId = 1L
        val modifierId = 2L
        val food = createEntity(FoodFixture(userId = creatorId).create())
        val request = FoodV1RequestFixture.Update().create()
        
        // when
        val throwable = catchThrowable {
            foodService.update(request, food.id, modifierId)
        }
        
        // then
        assertThat(throwable).isInstanceOf(BadRequestException::class.java)
    }
}
```

**테스트 포커스**:
- 권한 검증 로직
- Request → Command 변환
- 에러 케이스 처리

#### 4.2.3 Controller 테스트

**위치**: `application/balance-eat-api/src/test/kotlin/org/balanceeat/api/food/FoodV1ControllerTest.kt`

```kotlin
@WebMvcTest(FoodV1Controller::class)
@DisplayName("FoodV1Controller 테스트")
class FoodV1ControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockitoBean
    private lateinit var foodService: FoodService
    
    @Test
    fun `성공`() {
        // given
        val request = FoodV1Request.Create(/* ... */)
        val mockFoodDto = mockFoodDto()
        given(foodService.create(request, 1L)).willReturn(mockFoodDto)
        
        // when & then
        mockMvc.perform(
            post("/v1/foods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpected(jsonPath("$.data.name").value(mockFoodDto.name))
    }
    
    private fun mockFoodDto(): FoodDto {
        return FoodDto(/* 목 데이터 */)
    }
}
```

**테스트 포커스**:
- HTTP 요청/응답 검증
- 입력 유효성 검사
- API 스펙 준수

## 5. 개발 체크리스트

### ✅ Domain Layer
- [ ] Entity 구현 (`BaseEntity` 상속, `guard()` 검증)
- [ ] Command 클래스 정의
- [ ] DTO 클래스 구현 (`from()` 메서드 포함)
- [ ] Repository 인터페이스 정의
- [ ] Domain Service 구현 (`@DomainService`, DTO 반환)

### ✅ Application Layer  
- [ ] Application Service 구현 (권한 검증, Command 변환)
- [ ] API Payload 클래스 (Request/Response)
- [ ] API Spec 인터페이스 (OpenAPI 문서화)
- [ ] Controller 구현 (RESTful API 엔드포인트)

### ✅ Test Code
- [ ] Entity Fixture 생성
- [ ] Command Fixture 생성  
- [ ] API Request Fixture 생성
- [ ] Domain Service 테스트
- [ ] Application Service 테스트
- [ ] Controller 테스트 (`@WebMvcTest`)

### ✅ 명명 규칙 준수
- [ ] **Entity**: `{Domain}.kt`
- [ ] **Command**: `{Domain}Command.kt`
- [ ] **DTO**: `{Domain}Dto.kt`
- [ ] **Repository**: `{Domain}Repository.kt`
- [ ] **Domain Service**: `{Domain}DomainService.kt`
- [ ] **Application Service**: `{Domain}Service.kt`
- [ ] **Controller**: `{Domain}V1Controller.kt`
- [ ] **Payload**: `{Domain}V1Payload.kt`
- [ ] **API Spec**: `{Domain}V1ApiSpec.kt`

## 6. 참고 문서

- **[Project Structure Guidelines](project-structure.md)** - 프로젝트 구조 및 패키지 구성
- **[Entity Guidelines](entity-guidelines.md)** - 엔티티 설계 및 검증 규칙
- **[Fixture Guidelines](fixture-guidelines.md)** - 테스트 픽스처 패턴 및 사용법

## 7. 주요 원칙 요약

1. **계층 분리**: 각 계층의 책임을 명확히 분리
2. **DTO 패턴**: 도메인 엔티티 직접 노출 금지
3. **테스트 우선**: 모든 기능에 대한 포괄적 테스트 작성
4. **표준화**: 일관된 네이밍과 구조 패턴 준수
5. **문서화**: OpenAPI를 통한 API 명세 자동 생성

이 가이드를 따라 개발하면 일관성 있고 유지보수 가능한 고품질 코드를 작성할 수 있습니다.