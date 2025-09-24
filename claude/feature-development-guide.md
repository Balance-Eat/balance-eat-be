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
    var servingSize: Double,

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
        val servingSize: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val isAdminApproved: Boolean = false
    )

    data class Update(
        val id: Long,
        val name: String,
        val servingSize: Double,
        val unit: String,
        val carbohydrates: Double = 0.0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val isAdminApproved: Boolean = false
    )

    data class Search(
        val foodName: String?,
        val userId: Long?,
        val pageable: Pageable
    )
}
```

**검색 Command 설계 원칙**:
- **필터 조건**: 선택적 검색 조건들 (nullable 필드 사용)
- **페이징 정보**: Spring의 `Pageable` 인터페이스 활용

### 2.3 DTO 구현 (FoodDto.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodDto.kt`

```kotlin
data class FoodDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val userId: Long,
    val servingSize: Double,
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
                servingSize = food.servingSize,
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

### 2.3.1 검색 결과 DTO 구현 (FoodResult.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodResult.kt`

```kotlin
@QueryProjection
data class FoodSearchResult(
    val id: Long,
    val uuid: String,
    val name: String,
    val userId: Long,
    val servingSize: Double,
    val unit: String,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val isAdminApproved: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
```

**검색 결과 DTO 원칙**:
- `@QueryProjection` 어노테이션으로 QueryDSL 프로젝션 지원
- 검색 성능을 위해 필요한 필드만 선택적으로 포함
- 정렬 및 필터링에 사용되는 필드 포함 (createdAt, updatedAt 등)

### 2.4 Repository 인터페이스 (FoodRepository.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodRepository.kt`

```kotlin
interface FoodRepository : JpaRepository<Food, Long>, FoodRepositoryCustom {
    fun findByUuid(uuid: String): Food?
    fun findByUserId(userId: Long): List<Food>
}
```

### 2.4.1 Custom Repository 인터페이스 (FoodRepositoryCustom.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodRepositoryCustom.kt`

```kotlin
interface FoodRepositoryCustom {
    fun search(command: FoodCommand.Search): PageResponse<FoodSearchResult>
    fun findRecommendations(userId: Long, limit: Int): List<Food>
}
```

### 2.4.2 Custom Repository 구현 (FoodRepositoryCustomImpl.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodRepositoryCustomImpl.kt`

```kotlin
@Repository
class FoodRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : FoodRepositoryCustom {

    override fun search(command: FoodCommand.Search): PageResponse<FoodSearchResult> {
        val query = queryFactory
            .select(
                QFoodSearchResult(
                    food.id,
                    food.uuid,
                    food.name,
                    food.userId,
                    food.servingSize,
                    food.unit,
                    food.carbohydrates,
                    food.protein,
                    food.fat,
                    food.isAdminApproved,
                    food.createdAt,
                    food.updatedAt
                )
            )
            .from(food)
            .where(
                buildSearchConditions(command)
            )
            .orderBy(food.id.desc()) // 최신 순 정렬

        // 전체 개수 조회
        val total = query.fetchCount()

        // 페이징 적용
        val results = query
            .offset(command.pageable.offset)
            .limit(command.pageable.pageSize.toLong())
            .fetch()

        return PageResponse.of(
            items = results,
            totalItems = total,
            currentPage = command.pageable.pageNumber + 1,
            itemsPerPage = command.pageable.pageSize
        )
    }

    override fun findRecommendations(userId: Long, limit: Int): List<Food> {
        return queryFactory
            .selectFrom(food)
            .where(
                food.isAdminApproved.isTrue
                    .or(food.userId.eq(userId))
            )
            .orderBy(food.id.desc())
            .limit(limit.toLong())
            .fetch()
    }

    private fun buildSearchConditions(command: FoodCommand.Search): BooleanExpression? {
        val conditions = mutableListOf<BooleanExpression>()

        // 음식명 필터 (부분 검색, 대소문자 무시)
        command.foodName?.let { name ->
            conditions.add(food.name.containsIgnoreCase(name))
        }

        // 사용자 ID 필터 (본인 데이터 + 관리자 승인 데이터)
        command.userId?.let { userId ->
            conditions.add(
                food.userId.eq(userId)
                    .or(food.isAdminApproved.isTrue)
            )
        }

        return if (conditions.isEmpty()) {
            null
        } else {
            conditions.reduce { acc, condition -> acc.and(condition) }
        }
    }

    companion object {
        private val food = QFood.food
    }
}
```

**Repository 구현 원칙**:
- **QueryDSL 활용**: 동적 쿼리 구성을 위한 QueryDSL 사용
- **성능 최적화**: 필요한 필드만 선택하는 프로젝션 사용
- **페이징 처리**: Spring의 Pageable과 커스텀 PageResponse 활용
- **조건부 필터링**: 선택적 검색 조건을 위한 동적 쿼리 구성
- **정렬 기준**: 비즈니스 요구사항에 맞는 정렬 로직 (최신순 등)

### 2.5 Domain Service 구현 (FoodDomainService.kt)

**위치**: `domain/src/main/kotlin/org/balanceeat/domain/food/FoodDomainService.kt`

```kotlin
@DomainService
class FoodDomainService(
    private val foodRepository: FoodRepository
) {
    @Transactional
    fun create(command: FoodCommand.Create): FoodDto {
        val food = Food(
            name = command.name,
            uuid = command.uuid,
            userId = command.userId,
            servingSize = command.servingSize,
            unit = command.unit,
            carbohydrates = command.carbohydrates,
            protein = command.protein,
            fat = command.fat,
            isAdminApproved = command.isAdminApproved
        )
        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
    
    @Transactional
    fun update(command: FoodCommand.Update): FoodDto {
        val food = foodRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }
        
        food.name = command.name
        food.servingSize = command.servingSize
        food.unit = command.unit
        food.carbohydrates = command.carbohydrates
        food.protein = command.protein
        food.fat = command.fat
        food.isAdminApproved = command.isAdminApproved
        
        val savedFood = foodRepository.save(food)
        return FoodDto.from(savedFood)
    }
    
    @Transactional
    fun delete(id: Long) {
        val food = foodRepository.findById(id)
            .orElseThrow { EntityNotFoundException(DomainStatus.FOOD_NOT_FOUND) }
        foodRepository.delete(food)
    }
}
```

**Domain Service 책임 범위**:
- **포함해야 할 기능**: 순수한 도메인 CUD 로직 (Create, Update, Delete)
- **제외해야 할 기능**: 권한 검증, 비즈니스 규칙 (예: 사용자별 수정 권한 체크)
- **읽기 작업**: Application Service에서 Repository를 직접 사용하여 처리

**핵심 원칙**:
- `@DomainService` 어노테이션 필수
- 항상 DTO 반환 (엔티티 직접 반환 금지)
- 트랜잭션 범위 명시 (`@Transactional`)
- **공통 CUD 로직만 처리** - 어드민/유저 구분 없는 핵심 데이터 조작
- **권한 검증 및 비즈니스 규칙은 Application Service 담당**

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
    fun create(request: FoodV1Request.Create, creatorId: Long): FoodV1Response.Info {
        val domainResult = foodDomainService.create(
            command = FoodCommand.Create(
                uuid = request.uuid,
                userId = creatorId,
                name = request.name,
                servingSize = request.servingSize,
                unit = request.unit,
                carbohydrates = request.carbohydrates,
                protein = request.protein,
                fat = request.fat,
                isAdminApproved = false
            )
        )
        
        // API 모듈의 서비스에서 컨트롤러 응답 타입으로 변환
        return FoodV1Response.Info.from(domainResult)
    }

    @Transactional(readOnly = true)
    fun search(request: FoodV1Request.Search, pageable: Pageable): PageResponse<FoodSearchResult> {
        val command = FoodCommand.Search(
            foodName = request.foodName,
            userId = request.userId,
            pageable = pageable
        )
        
        val result = foodRepository.search(command)
        
        return PageResponse.from(result)
    }

    @Transactional(readOnly = true)
    fun getRecommendations(limit: Int): List<FoodDto> {
        val userId = 1L // TODO: 인증 연동 후 실제 사용자 ID 사용
        return foodRepository.findRecommendations(userId, limit)
            .map { FoodDto.from(it) }
    }
}
```

**Application Service 책임 범위**:
- **API Request → Domain Command 변환**: 외부 요청을 내부 도메인 언어로 변환
- **권한 검증**: 사용자별 수정/삭제 권한 체크 (예: 작성자만 수정 가능)
- **비즈니스 규칙 적용**: 도메인 공통 로직 외의 애플리케이션별 규칙
- **읽기 작업**: Repository를 직접 사용하여 조회 기능 구현
- **Domain Service 호출**: 순수 CUD 작업을 위한 도메인 서비스 위임
- **컨트롤러 타입 사용**: API 모듈의 서비스이므로 컨트롤러 요청/응답 타입 직접 사용 가능

**역할 분리**:
- **Domain Service**: 공통 CUD 로직 (권한/규칙 무관한 핵심 데이터 조작), **반드시 DTO 반환**
- **Application Service**: 권한 검증 + 비즈니스 규칙 + 읽기 작업, **컨트롤러 타입과의 변환**

### 3.2 API Payload 구현 (FoodV1Payload.kt)

**위치**: `application/balance-eat-api/src/main/kotlin/org/balanceeat/api/food/FoodV1Payload.kt`

```kotlin
class FoodV1Request {
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        val uuid: String,
        @field:NotNull(message = "음식명은 필수입니다")
        val name: String,
        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        val servingSize: Double,
        @field:NotNull(message = "단위는 필수입니다")
        val unit: String,
        val carbohydrates: Double? = null,
        val protein: Double? = null,
        val fat: Double? = null
    )

    data class Update(
        @field:NotNull(message = "음식명은 필수입니다")
        val name: String,
        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        val servingSize: Double,
        @field:NotNull(message = "단위는 필수입니다")
        val unit: String,
        val carbohydrates: Double? = null,
        val protein: Double? = null,
        val fat: Double? = null
    )

    data class Search(
        val foodName: String? = null,
        val userId: Long? = null
    )
}

class FoodV1Response {
    // 상세 정보
    data class Details(
        val id: Long,
        val uuid: String,
        val name: String,
        // ... 기타 필드들
    ) {
        companion object {
            fun from(food: FoodDto) = Details(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                // ... 필드 매핑
            )
        }
    }
}
```

**검색 관련 Payload 설계 원칙**:
- **Search Request**: 선택적 필터 조건들만 포함 (nullable 필드)
- **페이징 파라미터**: Controller에서 `@RequestParam`으로 직접 처리
- **Search Response**: Repository에서 반환하는 `FoodSearchResult`를 직접 사용
- **권한 처리**: Application Service에서 userId 주입

### 3.3 Controller 구현 (FoodV1Controller.kt)

**위치**: `application/balance-eat-api/src/main/kotlin/org/balanceeat/api/food/FoodV1Controller.kt`

```kotlin
@RestController
@RequestMapping("/v1/foods")
class FoodV1Controller(
    private val foodService: FoodService,
) {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: FoodV1Request.Create): ApiResponse<FoodV1Response.Info> {
        val result = foodService.create(request, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(result)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long,
                        @RequestBody @Valid request: FoodV1Request.Update): ApiResponse<FoodV1Response.Info> {
        val result = foodService.update(request, id, 1L) // TODO: 인증 연동 후 수정
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getDetails(@PathVariable id: Long): ApiResponse<FoodV1Response.Info> {
        val food = foodService.getDetails(id)
        return ApiResponse.success(FoodV1Response.Info.from(food))
    }

    @GetMapping("/recommendations")
    fun getRecommendations(@RequestParam(defaultValue = "10") limit: Int): ApiResponse<List<FoodV1Response.Info>> {
        val result = foodService.getRecommendations(limit)
            .map { FoodV1Response.Info.from(it) }

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

**검색 관련 Controller 설계 원칙**:
- **페이징 파라미터**: `@RequestParam`으로 직접 처리하여 Spring의 Pageable 생성
- **선택적 필터**: `required = false`와 기본값 설정으로 선택적 검색 조건 처리
- **권한 처리**: 현재 사용자 ID를 Application Service에 전달
- **응답 타입**: 검색은 `FoodSearchResult` 직접 반환, 추천은 `FoodDto` → `Info` 변환

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
    var servingSize: Double = 100.0,
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
            servingSize = servingSize,
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
        var servingSize: Double = 100.0,
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
                servingSize = servingSize,
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
        val servingSize: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0
    ): TestFixture<FoodV1Request.Create> {
        override fun create(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = uuid,
                name = name,
                servingSize = servingSize,
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
- **CUD 로직 검증**: Create, Update, Delete 기능의 정확성
- **데이터 변환 검증**: Command → Entity, Entity → DTO 변환
- **트랜잭션 동작 확인**: 데이터베이스 상태 변경 검증
- **도메인 규칙 검증**: Entity의 guard() 메서드를 통한 비즈니스 규칙

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
- **권한 검증 로직**: 작성자만 수정/삭제 가능 등의 비즈니스 규칙
- **비즈니스 규칙**: 도메인 공통 로직 외의 애플리케이션별 규칙
- **읽기 작업**: Repository를 통한 조회 기능의 정확성
- **Request → Command 변환**: API 요청을 도메인 커맨드로 올바르게 변환

#### 4.2.3 Controller 테스트

**위치**: `application/balance-eat-api/src/test/kotlin/org/balanceeat/api/food/FoodV1ControllerTest.kt`

```kotlin
@WebMvcTest(FoodV1Controller::class)
class FoodV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    // 🔑 핵심: 컨트롤러의 모든 의존성을 MockkBean으로 설정
    @MockkBean(relaxed = true)
    private lateinit var foodService: FoodService
    
    @Nested
    @DisplayName("POST /v1/foods - 음식 생성")
    inner class CreateTest {
        @Test
        fun success() {
            // 🔑 핵심: 해당 테스트에서 실제 호출되는 메서드만 every로 Mock
            val request = mockCreateRequest()
            every { foodService.create(any(), any()) } returns mockFoodDto()
            
            given()
                .body(request)
                .post("/v1/foods")
                .then()
                .log().all()
                .apply(documentationHandler())
                .status(HttpStatus.CREATED)
        }
        
        private fun mockCreateRequest(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = "test-uuid-456",
                name = "새로운 음식",
                servingSize = 150.0,
                unit = "ml"
            )
        }
    }

    @Nested
    @DisplayName("GET /v1/foods/{id} - 음식 조회")
    inner class GetDetailsTest {
        @Test
        fun success() {
            // 🔑 이 테스트에서는 getDetails()만 Mock (create는 Mock하지 않음)
            every { foodService.getDetails(any()) } returns mockFoodDto()
            
            given()
                .get("/v1/foods/1")
                .then()
                .log().all()
                .apply(documentationHandler())
                .status(HttpStatus.OK)
        }
    }
    
    private fun mockFoodDto(): FoodDto {
        return FoodDto(
            id = 1L,
            uuid = "test-uuid-123",
            name = "테스트 음식",
            userId = 1L,
            servingSize = 100.0,
            unit = "g",
            carbohydrates = 25.0,
            protein = 8.0,
            fat = 3.0,
            isAdminApproved = false,
            createdAt = LocalDateTime.now()
        )
    }
}
```

**🔑 핵심 Mock 설정 원칙**:
1. **컨트롤러의 모든 의존성을 `@MockkBean`으로 설정**
2. **각 테스트 메서드에서 실제 호출되는 메서드만 `every`로 Mock**
3. **불필요한 Mock 설정 금지** (성능 저하 및 테스트 복잡도 증가)

**테스트 포커스**:
- HTTP 요청/응답 검증 (성공 케이스만)
- API 문서화 (RestDocs 통합)
- 컨트롤러 레이어만 격리 테스트

**⚠️ 상세한 컨트롤러 테스트 가이드라인**: [Controller Test Guidelines](controller-test-guidelines.md) 참조

### 4.2.4 HTTP 파일 (API 수동 테스트)

**위치**: `http/{domain}-api.http`

```http
### Food 생성 API 테스트 (성공 케이스)

POST {{baseUrl}}/{{apiVersion}}/foods
Content-Type: application/json

{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "name": "김치찌개",
  "servingSize": 200.0,
  "unit": "g",
  "carbohydrates": 15.0,
  "protein": 12.0,
  "fat": 8.0
}

###

### Food 수정 API 테스트 (성공 케이스)

PUT {{baseUrl}}/{{apiVersion}}/foods/1
Content-Type: application/json

{
  "name": "수정된 김치찌개",
  "servingSize": 250.0,
  "unit": "g",
  "carbohydrates": 18.0,
  "protein": 15.0,
  "fat": 10.0
}

###

### Food 조회 API 테스트 (성공 케이스)

GET {{baseUrl}}/{{apiVersion}}/foods/1
Accept: application/json

###

### Food 검색 API 테스트 (성공 케이스)

GET {{baseUrl}}/{{apiVersion}}/foods/search?foodName=김치&page=0&size=10
Accept: application/json

###

### Food 추천 목록 API 테스트 (성공 케이스)

GET {{baseUrl}}/{{apiVersion}}/foods/recommendations?limit=10
Accept: application/json
```

**작성 원칙**:
- **성공 케이스만 작성** (에러 케이스는 테스트 코드에서 검증)
- 각 주요 엔드포인트마다 하나의 요청만 작성
- 환경 변수 활용 (`{{baseUrl}}`, `{{apiVersion}}`)
- 실제 사용 가능한 데이터로 작성

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
  - [ ] 컨트롤러의 모든 의존성을 `@MockkBean`으로 설정
  - [ ] 각 테스트에서 실제 호출되는 메서드만 `every`로 Mock

### ✅ API 테스트 파일
- [ ] HTTP 파일 생성 (`.http` 확장자, 성공 케이스만)

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
- **[Controller Test Guidelines](controller-test-guidelines.md)** - 컨트롤러 테스트 작성 및 Mock 설정 가이드

## 7. 주요 원칙 요약

1. **계층 분리**: 각 계층의 책임을 명확히 분리
2. **DTO 패턴**: 도메인 엔티티 직접 노출 금지
3. **테스트 우선**: 모든 기능에 대한 포괄적 테스트 작성
4. **표준화**: 일관된 네이밍과 구조 패턴 준수
5. **문서화**: OpenAPI를 통한 API 명세 자동 생성

이 가이드를 따라 개발하면 일관성 있고 유지보수 가능한 고품질 코드를 작성할 수 있습니다.