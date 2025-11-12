# Balance-Eat Domain Layer 표준 템플릿

## 필수 규칙
- **검증 로직**: 모든 엔티티는 `guard()` 메서드에서 비즈니스 규칙 검증을 수행합니다

## 목적

Balance-Eat 프로젝트의 도메인 레이어 표준 템플릿을 제공합니다. 각 도메인은 독립적인 패키지 구조를 가지며, Kotlin + Spring Data JPA + Querydsl을 기반으로 합니다.

---

## 표준 패키지 구조

```

domain/src/main/kotlin/org/balanceeat/domain/{domain}/
├── {Domain}.kt                      # 엔티티
├── {Domain}Command.kt                # Command 객체 (CUD)
├── {Domain}Query.kt                  # Query 객체 (조회, 필요시)
├── {Domain}Result.kt                 # Result 객체 (조회 결과)
├── {Domain}Writer.kt                 # 도메인 CUD 컴포넌트
├── {Domain}Reader.kt                 # 도메인 조회 컴포넌트
├── {Domain}Repository.kt             # Repository 인터페이스
├── {Domain}RepositoryCustom.kt       # Querydsl 커스텀 인터페이스 (필요시)
└── {Domain}RepositoryCustomImpl.kt   # Querydsl 구현 (필요시)

domain/src/testFixtures/kotlin/org/balanceeat/domain/{domain}/
├── {Domain}Fixture.kt                # 엔티티 픽스처
├── {Domain}CommandFixture.kt         # Command 픽스처
├── {Domain}QueryFixture.kt           # Query 픽스처
└── {Domain}ResultFixture.kt          # Result 픽스처

domain/src/test/kotlin/org/balanceeat/domain/{domain}/
├── {Domain}Test.kt                   # 엔티티 테스트 (필요시)
└── {Domain}DomainServiceTest.kt      # 도메인 서비스 테스트
```

**파일 설명**:
- **{Domain}.kt**: JPA 엔티티 및 Enum 타입 정의
- **{Domain}Command.kt**: Create, Update, Delete 등 명령 객체 (CUD)
- **{Domain}Query.kt**: 복잡한 조회 조건 객체 (필요시, 예: Search)
- **{Domain}Result.kt**: 도메인 조회 결과 객체 (Entity → Result 변환)
- **{Domain}Writer.kt**: 도메인 CUD(생성/수정/삭제) 비즈니스 로직 처리
- **{Domain}Reader.kt**: 도메인 조회(Read) 비즈니스 로직 처리
- **{Domain}Repository.kt**: 기본 Repository 인터페이스
- **{Domain}RepositoryCustom.kt**: Querydsl 커스텀 쿼리 인터페이스
- **{Domain}RepositoryCustomImpl.kt**: Querydsl 구현체

---

## Base Entity 가이드

프로젝트는 `BaseEntity`를 사용하여 공통 감사 필드를 관리합니다.

### BaseEntity

```kotlin
@MappedSuperclass
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    open fun guard() = Unit

    @PrePersist
    private fun prePersist() {
        guard()
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    private fun preUpdate() {
        guard()
        val now = LocalDateTime.now()
        updatedAt = now
    }
}

const val NEW_ID = 0L
```

**제공 필드**:
- `createdAt`: 생성일시 (자동 설정)
- `updatedAt`: 수정일시 (자동 갱신)

**검증 시점**:
- `@PrePersist`: 엔티티 저장 전
- `@PreUpdate`: 엔티티 수정 전

---

## 템플릿 1: Entity ({Domain}.kt)

```kotlin
package org.balanceeat.domain.example

import jakarta.persistence.*
import org.balanceeat.domain.config.BaseEntity
import org.balanceeat.domain.config.NEW_ID

@Entity
@Table(name = "example")
class Example(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = NEW_ID,

    @Column(nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: Status = Status.ACTIVE,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = true)
    var description: String? = null
) : BaseEntity() {

    companion object {
        const val MAX_NAME_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 1000
    }

    override fun guard() {
        require(name.isNotBlank()) { "이름은 필수값입니다" }
        require(name.length <= MAX_NAME_LENGTH) { "이름은 ${MAX_NAME_LENGTH}자를 초과할 수 없습니다" }
        require(userId > 0) { "사용자 ID는 0보다 큰 값이어야 합니다" }

        description?.let {
            require(it.length <= MAX_DESCRIPTION_LENGTH) {
                "설명은 ${MAX_DESCRIPTION_LENGTH}자를 초과할 수 없습니다"
            }
        }
    }

    fun update(name: String,
               description: String?) {
        this.name = name
        this.description = description
    }
    
    fun deactivate(user: User) {
        // 비즈니스 로직 메서드 예시
        
        // 1. 검증
        if (user.id != this.userId || user.type != User.Type.ADMIN) {
            throw DomainException(DomainStatus.UNAUTHORIZED_ACTION)
        }
        
        // 2. 상태 변경
        this.status = Status.INACTIVE
    }

    enum class Status {
        ACTIVE,
        INACTIVE
    }
}
```

**핵심 패턴**:
- `BaseEntity` 상속으로 createdAt/updatedAt 자동 관리
- `guard()` 오버라이드로 비즈니스 규칙 검증
- Enum은 `EnumType.STRING`으로 매핑
- 상수는 companion object에 정의
- 상태 변경 메서드 제공
- 메서드는 비즈니스 로직 중심으로 작성 & 객체 지향 설계 준수

---

## 템플릿 2: Command ({Domain}Command.kt)

```kotlin
package org.balanceeat.domain.example

class ExampleCommand {

    data class Create(
        val name: String,
        val userId: Long,
        val status: Example.Status,
        val description: String? = null
    )

    data class Update(
        val id: Long,
        val name: String,
        val status: Example.Status,
        val description: String? = null
    )
}
```

**핵심 패턴**:
- Create/Update/Delete 등 data class로 정의
- 필수값과 선택값 구분

---

## 템플릿 3: Result ({Domain}Result.kt)

도메인 레이어의 조회 결과를 나타내는 객체들입니다. Entity를 직접 반환하지 않고 Result 객체로 변환하여 반환합니다.

```kotlin
package org.balanceeat.domain.example

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ExampleResult(
    val id: Long,
    val name: String,
    val status: Example.Status,
    val userId: Long,
    val description: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(example: Example): ExampleResult {
            return ExampleResult(
                id = example.id,
                name = example.name,
                status = example.status,
                userId = example.userId,
                description = example.description,
                createdAt = example.createdAt
            )
        }
    }
}

@QueryProjection
data class ExampleSearchResult(
    val id: Long,
    val name: String,
    val status: Example.Status,
    val userId: Long,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ExampleDetails(
    val id: Long,
    val name: String,
    val status: Example.Status,
    val userId: Long,
    val description: String?,
    val relatedData: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(example: Example): ExampleDetails {
            return ExampleDetails(
                id = example.id,
                name = example.name,
                status = example.status,
                userId = example.userId,
                description = example.description,
                relatedData = example.relatedData,
                createdAt = example.createdAt,
                updatedAt = example.updatedAt
            )
        }
    }
}

data class ExampleSummary(
    val id: Long,
    val name: String,
    val status: Example.Status
) {
    companion object {
        fun from(example: Example): ExampleSummary {
            return ExampleSummary(
                id = example.id,
                name = example.name,
                status = example.status
            )
        }
    }
}
```

**핵심 패턴**:
- **{Domain}Result**: 도메인 서비스의 기본 반환 타입 (CUD 작업 결과)
- **{Domain}SearchResult**: Querydsl 검색 결과용 Projection (`@QueryProjection`)
- **{Domain}Details**: 상세 정보 조회용 (연관 데이터 포함)
- **{Domain}Summary**: 목록 조회용 간략 정보
- companion object의 `from()` 팩토리 메서드로 Entity → Result 변환

**사용 시나리오**:
- **Result**: Domain Service의 create/update 반환, 단순 조회
- **SearchResult**: Repository의 Querydsl 검색 결과 (성능 최적화)
- **Details**: 연관 데이터를 포함한 상세 조회
- **Summary**: 목록 화면용 간략 정보

**명명 규칙**:
- 기본: `{Domain}Result`
- 검색: `{Domain}SearchResult` (with `@QueryProjection`)
- 상세: `{Domain}Details`
- 요약: `{Domain}Summary`
- 통계: `{Domain}StatsResult`, `{Domain}AggregateResult`

---

## 템플릿 4: Query ({Domain}Query.kt)

복잡한 조회 조건이 필요한 경우에만 작성합니다. 단순 조회는 Repository 메서드로 충분합니다.

```kotlin
package org.balanceeat.domain.example

import org.springframework.data.domain.Pageable

class ExampleQuery {

    data class Search(
        val name: String?,
        val status: Example.Status?,
        val userId: Long?,
        val pageable: Pageable
    )

    data class Filter(
        val userId: Long,
        val statuses: List<Example.Status>,
        val fromDate: LocalDate,
        val toDate: LocalDate
    )
}
```

**핵심 패턴**:
- **Command vs Query 분리**: CUD는 Command, 조회는 Query
- **복잡한 조건만**: 단순 조회는 Repository 메서드 사용
- **Pageable 포함**: 페이징이 필요한 검색에는 Pageable 포함
- **Optional 조건**: nullable 필드로 선택적 조건 표현

**작성 시기**:
- ✅ 여러 조건의 AND/OR 조합이 필요한 경우
- ✅ 페이징/정렬이 필요한 검색
- ✅ 날짜 범위, 상태 목록 등 복잡한 필터링
- ❌ 단일 필드 조회 (Repository 메서드로 충분)
- ❌ ID로 단건 조회 (Repository의 `findById` 사용)

---

## 템플릿 5: Repository ({Domain}Repository.kt)

```kotlin
package org.balanceeat.domain.example

import org.springframework.data.jpa.repository.JpaRepository

interface ExampleRepository : JpaRepository<Example, Long>, ExampleRepositoryCustom {
    fun findByUserId(userId: Long): List<Example>
    fun findByUserIdAndStatus(userId: Long, status: Example.Status): List<Example>
    fun existsByUserIdAndName(userId: Long, name: String): Boolean
}
```

**핵심 패턴**:
- `JpaRepository<Entity, ID>` 상속
- 간단한 조회에 경우 JPA 쿼리 메서드 정의
- 복잡한 커스텀 쿼리 필요시 `ExampleRepositoryCustom` 추가 상속
- Spring Data JPA 쿼리 메서드 활용
---

## 템플릿 6: Repository Custom (Querydsl)

### {Domain}RepositoryCustom.kt

```kotlin
package org.balanceeat.domain.example

import java.time.LocalDate
import java.time.YearMonth

interface ExampleRepositoryCustom {
    fun findDailyExamples(userId: Long, date: LocalDate): List<Example>
    fun findMonthlyExamples(userId: Long, yearMonth: YearMonth): List<Example>
    fun existsByUserIdAndDate(userId: Long, date: LocalDate): Boolean
}
```

### {Domain}RepositoryCustomImpl.kt

```kotlin
package org.balanceeat.domain.example

import com.querydsl.jpa.impl.JPAQueryFactory
import org.balanceeat.domain.example.QExample.Companion.example
import java.time.LocalDate
import java.time.YearMonth

class ExampleRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ExampleRepositoryCustom {

    override fun findDailyExamples(userId: Long, date: LocalDate): List<Example> {
        return jpaQueryFactory
            .selectFrom(example)
            .where(
                example.userId.eq(userId)
                    .and(example.createdAt.dayOfMonth().eq(date.dayOfMonth))
                    .and(example.createdAt.month().eq(date.monthValue))
                    .and(example.createdAt.year().eq(date.year))
            )
            .orderBy(example.createdAt.asc())
            .fetch()
    }

    override fun findMonthlyExamples(userId: Long, yearMonth: YearMonth): List<Example> {
        return jpaQueryFactory
            .selectFrom(example)
            .where(
                example.userId.eq(userId)
                    .and(example.createdAt.month().eq(yearMonth.monthValue))
                    .and(example.createdAt.year().eq(yearMonth.year))
            )
            .orderBy(example.createdAt.asc())
            .fetch()
    }

    override fun existsByUserIdAndDate(userId: Long, date: LocalDate): Boolean {
        return jpaQueryFactory
            .selectOne()
            .from(example)
            .where(
                example.userId.eq(userId)
                    .and(example.createdAt.dayOfMonth().eq(date.dayOfMonth))
                    .and(example.createdAt.month().eq(date.monthValue))
                    .and(example.createdAt.year().eq(date.year))
            )
            .fetchFirst() != null
    }
}
```

**핵심 패턴**:
- `JPAQueryFactory` 주입
- Q-Type 사용 (예: `QExample.example`)
- where 조건 체이닝 (`.and()`)
- 존재 여부는 `selectOne()` + `fetchFirst() != null`

---

## 템플릿 7: Domain Service ({Domain}DomainService.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.exception.DomainException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@DomainService
class ExampleDomainService(
    private val exampleRepository: ExampleRepository
) {
    @Transactional
    fun create(command: ExampleCommand.Create): ExampleResult {
        validateCreation(command)

        val example = Example(
            name = command.name,
            userId = command.userId,
            status = command.status,
            description = command.description
        )

        val savedExample = exampleRepository.save(example)
        return ExampleResult.from(savedExample)
    }

    @Transactional
    fun update(command: ExampleCommand.Update): ExampleResult {
        val example = exampleRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.EXAMPLE_NOT_FOUND) }

        example.update(
            name = command.name,
            description = command.description
        )

        val savedExample = exampleRepository.save(example)
        return ExampleResult.from(savedExample)
    }

    @Transactional
    fun delete(id: Long) {
        val example = exampleRepository.findById(id)
            .ifPresent{ exampleRepository.delete(it) }
    }

    private fun validateCreation(command: ExampleCommand.Create) {
        if (exampleRepository.existsByUserIdAndName(command.userId, command.name)) {
            throw DomainException(DomainStatus.EXAMPLE_ALREADY_EXISTS)
        }
    }
}
```

**핵심 패턴**:
- `@DomainService` 어노테이션
- `@Transactional(readOnly = true)` 조회 메서드에 적용
- `@Transactional` 쓰기 메서드에 적용
- 검증 로직은 private 메서드로 분리
- `findById().orElseThrow()` 또는 `findByIdOrNull()` + Elvis 연산자로 예외 처리
- **반환 타입**: CUD 메서드는 Result 객체 반환 (Entity 직접 반환 금지)
- **Entity → Result 변환**: `Result.from(entity)` 패턴 사용

---

## Writer/Reader 패턴 개요

### 패턴 목적

기존 DomainService 패턴을 **CUD(Writer)**와 **Read(Reader)**로 명확히 분리하여 책임을 구분합니다.

### 핵심 차이점

| 항목 | 기존 (DomainService) | 신규 (Writer/Reader) |
|------|---------------------|---------------------|
| **파일 구조** | `{Domain}DomainService.kt` | `{Domain}Writer.kt` + `{Domain}Reader.kt` |
| **책임 범위** | CUD + Read 혼재 | CUD와 Read 명확히 분리 |
| **트랜잭션** | 메서드별 `@Transactional` | Writer: `@Transactional`, Reader: `@Transactional(readOnly = true)` |
| **의존성** | Repository 의존 | Repository 의존 (동일) |
| **반환 타입** | Result 객체 | Result 객체 (동일) |

### Writer와 Reader의 역할

**Writer ({Domain}Writer.kt)**:
- **책임**: Create, Update, Delete 비즈니스 로직
- **트랜잭션**: `@Transactional` (쓰기)
- **의존성**: Repository (CUD 메서드 사용)
- **반환**: Result 객체 (CUD 결과)

**Reader ({Domain}Reader.kt)**:
- **책임**: 조회 비즈니스 로직 (단순 조회, 복잡한 검색)
- **트랜잭션**: `@Transactional(readOnly = true)` (읽기 전용)
- **의존성**: Repository (조회 메서드 사용)
- **반환**: Result 객체 또는 Result 리스트

### 마이그레이션 고려사항

#### Reader 마이그레이션 이슈

**문제점**: 기존 코드에서 Repository의 `findById()`, `findByXxx()` 등을 직접 사용하는 경우가 많음

**해결 방안** (추후 결정 필요):

1. **Option A - 점진적 마이그레이션**
   - 기존 Repository 직접 사용 유지
   - 신규 도메인부터 Reader 패턴 적용
   - 장점: 점진적 전환 가능, 기존 코드 변경 최소화
   - 단점: 일관성 부족, 두 가지 패턴 공존

2. **Option B - Reader 래퍼 메서드 제공**
   ```kotlin
   @Component
   class ExampleReader(
       private val exampleRepository: ExampleRepository
   ) {
       @Transactional(readOnly = true)
       fun findById(id: Long): ExampleResult? {
           return exampleRepository.findByIdOrNull(id)?.let { ExampleResult.from(it) }
       }

       @Transactional(readOnly = true)
       fun findByUserId(userId: Long): List<ExampleResult> {
           return exampleRepository.findByUserId(userId).map { ExampleResult.from(it) }
       }
   }
   ```
   - 장점: Repository 메서드를 Reader로 래핑, 명확한 책임 분리
   - 단점: 보일러플레이트 코드 증가

3. **Option C - Repository 직접 사용 허용**
   - Reader는 복잡한 비즈니스 로직이 포함된 조회만 담당
   - 단순 `findById()` 등은 Repository 직접 사용 허용
   - 장점: 실용적, 불필요한 래핑 방지
   - 단점: 책임 경계가 모호해질 수 있음

**권장 방향**: Option C (실용적 접근) + 신규 도메인은 Option B 적용

---

## 템플릿 7A: Writer ({Domain}Writer.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ExampleWriter(
    private val exampleRepository: ExampleRepository
) {
    @Transactional
    fun create(command: ExampleCommand.Create): ExampleResult {
        validateCreation(command)

        val example = Example(
            name = command.name,
            userId = command.userId,
            status = command.status,
            description = command.description
        )

        val savedExample = exampleRepository.save(example)
        return ExampleResult.from(savedExample)
    }

    @Transactional
    fun update(command: ExampleCommand.Update): ExampleResult {
        val example = exampleRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.EXAMPLE_NOT_FOUND) }

        example.update(
            name = command.name,
            description = command.description
        )

        val savedExample = exampleRepository.save(example)
        return ExampleResult.from(savedExample)
    }

    @Transactional
    fun delete(id: Long) {
        val example = exampleRepository.findById(id)
            .orElseThrow { EntityNotFoundException(DomainStatus.EXAMPLE_NOT_FOUND) }

        exampleRepository.delete(example)
    }

    private fun validateCreation(command: ExampleCommand.Create) {
        if (exampleRepository.existsByUserIdAndName(command.userId, command.name)) {
            throw DomainException(DomainStatus.EXAMPLE_ALREADY_EXISTS)
        }
    }
}
```

**핵심 패턴**:
- `@Component` 어노테이션 (Spring 빈 등록)
- `@Transactional` 모든 메서드에 적용 (쓰기 작업)
- 검증 로직은 private 메서드로 분리
- **반환 타입**: Result 객체 반환 (Entity 직접 반환 금지)
- **Entity → Result 변환**: `Result.from(entity)` 패턴 사용

---

## 템플릿 7B: Reader ({Domain}Reader.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ExampleReader(
    private val exampleRepository: ExampleRepository
) {
    @Transactional(readOnly = true)
    fun findById(id: Long): ExampleResult {
        val example = exampleRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(DomainStatus.EXAMPLE_NOT_FOUND)

        return ExampleResult.from(example)
    }

    @Transactional(readOnly = true)
    fun findByUserId(userId: Long): List<ExampleResult> {
        return exampleRepository.findByUserId(userId)
            .map { ExampleResult.from(it) }
    }

    @Transactional(readOnly = true)
    fun search(query: ExampleQuery.Search): Page<ExampleSearchResult> {
        return exampleRepository.search(query)
    }

    @Transactional(readOnly = true)
    fun existsByUserIdAndName(userId: Long, name: String): Boolean {
        return exampleRepository.existsByUserIdAndName(userId, name)
    }
}
```

**핵심 패턴**:
- `@Component` 어노테이션 (Spring 빈 등록)
- `@Transactional(readOnly = true)` 모든 메서드에 적용 (읽기 전용)
- **반환 타입**: Result 객체 또는 Result 리스트 (Entity 직접 반환 금지)
- **Entity → Result 변환**: `Result.from(entity)` 패턴 사용
- 복잡한 검색은 Query 객체 활용

**Reader 작성 가이드**:
1. **단순 조회**: `findById()`, `findByXxx()` → Repository 직접 호출 후 Result 변환
2. **복잡한 검색**: Query 객체를 받아 Repository의 커스텀 메서드 호출
3. **존재 여부 확인**: `existsByXxx()` → Repository 메서드 직접 호출
4. **비즈니스 로직 포함 조회**: Repository 조회 + 추가 로직 후 Result 반환

---

## 템플릿 8: Test Fixture (통합 가이드)

도메인 레이어의 모든 픽스쳐는 동일한 핵심 패턴을 따릅니다. 픽스쳐 타입별로 4가지가 있습니다:

1. **Entity Fixture** (`{Domain}Fixture.kt`): 엔티티 테스트 데이터
2. **Command Fixture** (`{Domain}CommandFixture.kt`): Command 객체 테스트 데이터
3. **Query Fixture** (`{Domain}QueryFixture.kt`): Query 객체 테스트 데이터 (필요시)
4. **Result Fixture** (`{Domain}ResultFixture.kt`): Result 객체 테스트 데이터 (필요시)

### 공통 핵심 패턴

모든 픽스쳐는 다음 패턴을 따릅니다:

```kotlin
// 1. TestFixture<T> 인터페이스 구현 (제네릭 타입 명시)
class SomeFixture(...) : TestFixture<SomeType> {
    override fun create(): SomeType { ... }
}

// 2. 기본값을 가진 생성자 파라미터
class SomeFixture(
    var field1: Type1 = defaultValue1,
    var field2: Type2 = defaultValue2,
    ...
)

// 3. create() 메서드로 객체 생성
override fun create(): SomeType {
    return SomeType(field1, field2, ...)
}

// 4. 최상위 함수로 DSL 스타일 제공
fun someFixture(block: SomeFixture.() -> Unit = {}): SomeType {
    return SomeFixture().apply(block).create()
}
```

### 8-1. Entity Fixture ({Domain}Fixture.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.config.TestFixture

class ExampleFixture(
    var id: Long = 1L,
    var name: String = "테스트 예제",
    var status: Example.Status = Example.Status.ACTIVE,
    var userId: Long = 1L,
    var description: String? = null
) : TestFixture<Example> {
    override fun create(): Example {
        return Example(
            id = 0L,
            name = name,
            status = status,
            userId = userId,
            description = description
        )
    }
}

fun exampleFixture(block: ExampleFixture.() -> Unit = {}): Example {
    return ExampleFixture().apply(block).create()
}
```

**특징**:
- ID는 생성자 파라미터로 직접 전달하여 설정
- 함수명 패턴: `{domain}Fixture`

### 8-2. Command Fixture ({Domain}CommandFixture.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.config.TestFixture

class ExampleCommandFixture {

    class Create(
        var name: String = "테스트 예제",
        var userId: Long = 1L,
        var status: Example.Status = Example.Status.ACTIVE,
        var description: String? = null
    ) : TestFixture<ExampleCommand.Create> {
        override fun create(): ExampleCommand.Create {
            return ExampleCommand.Create(
                name = name,
                userId = userId,
                status = status,
                description = description
            )
        }
    }

    class Update(
        var id: Long = 1L,
        var name: String = "수정된 예제",
        var status: Example.Status = Example.Status.INACTIVE,
        var description: String? = null
    ) : TestFixture<ExampleCommand.Update> {
        override fun create(): ExampleCommand.Update {
            return ExampleCommand.Update(
                id = id,
                name = name,
                status = status,
                description = description
            )
        }
    }
}

fun exampleCreateCommandFixture(block: ExampleCommandFixture.Create.() -> Unit = {}): ExampleCommand.Create {
    return ExampleCommandFixture.Create().apply(block).create()
}

fun exampleUpdateCommandFixture(block: ExampleCommandFixture.Update.() -> Unit = {}): ExampleCommand.Update {
    return ExampleCommandFixture.Update().apply(block).create()
}
```

**특징**:
- Command별 Fixture 클래스 중첩 (Create, Update 등)
- 함수명 패턴: `{domain}{Operation}CommandFixture`

### 8-3. Query Fixture ({Domain}QueryFixture.kt)

복잡한 조회 조건이 있는 경우에만 작성합니다.

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.config.TestFixture
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class ExampleQueryFixture {

    class Search(
        var name: String? = null,
        var status: Example.Status? = null,
        var userId: Long? = null,
        var pageable: Pageable = PageRequest.of(0, 10)
    ) : TestFixture<ExampleQuery.Search> {
        override fun create(): ExampleQuery.Search {
            return ExampleQuery.Search(
                name = name,
                status = status,
                userId = userId,
                pageable = pageable
            )
        }
    }

    class Filter(
        var userId: Long = 1L,
        var statuses: List<Example.Status> = listOf(Example.Status.ACTIVE),
        var fromDate: LocalDate = LocalDate.now().minusDays(7),
        var toDate: LocalDate = LocalDate.now()
    ) : TestFixture<ExampleQuery.Filter> {
        override fun create(): ExampleQuery.Filter {
            return ExampleQuery.Filter(
                userId = userId,
                statuses = statuses,
                fromDate = fromDate,
                toDate = toDate
            )
        }
    }
}

fun exampleSearchQueryFixture(block: ExampleQueryFixture.Search.() -> Unit = {}): ExampleQuery.Search {
    return ExampleQueryFixture.Search().apply(block).create()
}

fun exampleFilterQueryFixture(block: ExampleQueryFixture.Filter.() -> Unit = {}): ExampleQuery.Filter {
    return ExampleQueryFixture.Filter().apply(block).create()
}
```

**특징**:
- Query별 Fixture 클래스 중첩 (Search, Filter 등)
- 함수명 패턴: `{domain}{Operation}QueryFixture`

### 8-4. Result Fixture ({Domain}ResultFixture.kt)

Result 객체가 많거나 복잡한 경우에만 작성합니다.

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.config.TestFixture
import java.time.LocalDateTime

class ExampleResultFixture {

    class Basic(
        var id: Long = 1L,
        var name: String = "테스트 예제",
        var status: Example.Status = Example.Status.ACTIVE,
        var userId: Long = 1L,
        var description: String? = null,
        var createdAt: LocalDateTime = LocalDateTime.now()
    ) : TestFixture<ExampleResult> {
        override fun create(): ExampleResult {
            return ExampleResult(
                id = id,
                name = name,
                status = status,
                userId = userId,
                description = description,
                createdAt = createdAt
            )
        }
    }

    class Details(
        var id: Long = 1L,
        var name: String = "테스트 예제",
        var status: Example.Status = Example.Status.ACTIVE,
        var userId: Long = 1L,
        var description: String? = null,
        var relatedData: String? = "관련 데이터",
        var createdAt: LocalDateTime = LocalDateTime.now(),
        var updatedAt: LocalDateTime = LocalDateTime.now()
    ) : TestFixture<ExampleDetails> {
        override fun create(): ExampleDetails {
            return ExampleDetails(
                id = id,
                name = name,
                status = status,
                userId = userId,
                description = description,
                relatedData = relatedData,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

fun exampleResultFixture(block: ExampleResultFixture.Basic.() -> Unit = {}): ExampleResult {
    return ExampleResultFixture.Basic().apply(block).create()
}

fun exampleDetailsFixture(block: ExampleResultFixture.Details.() -> Unit = {}): ExampleDetails {
    return ExampleResultFixture.Details().apply(block).create()
}
```

**특징**:
- Result 타입별 Fixture 클래스 중첩 (Basic, Details, Summary 등)
- 함수명 패턴: `{domain}{ResultType}Fixture`

### 픽스쳐 작성 원칙

1. **TestFixture<T> 구현**: 모든 픽스쳐는 `TestFixture<T>` 인터페이스 구현
2. **기본값 제공**: 생성자 파라미터에 실제적인 기본값 제공
3. **DSL 함수**: 최상위 함수로 간편한 사용 지원
4. **한글 데이터**: 테스트 데이터는 한글로 작성하여 가독성 향상
5. **리플렉션**: Entity Fixture만 `FixtureReflectionUtils.setId()` 사용

### 픽스쳐 작성 시기
Entity, Command, Query, Result 객체가 생성될거나 변경될때

---

## 템플릿 10: Entity Test ({Domain}Test.kt)

엔티티의 비즈니스 로직 메서드를 테스트합니다. 복잡한 로직(if문 분기, 권한 체크, 계산)이 포함된 메서드만 테스트합니다.

```kotlin
package org.balanceeat.domain.example

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.exception.DomainException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ExampleTest {

    @Nested
    @DisplayName("update 메서드 테스트")
    inner class UpdateTest {

        @Test
        fun `예제 정보를 수정할 수 있다`() {
            // given
            val example = ExampleFixture.create {
                name = "수정 전 이름"
                userId = 1L
                status = Example.Status.ACTIVE
                description = "수정 전 설명"
            }

            // when
            example.update(
                name = "수정 후 이름",
                description = "수정 후 설명"
            )

            // then
            assertThat(example.name).isEqualTo("수정 후 이름")
            assertThat(example.description).isEqualTo("수정 후 설명")
        }
    }

    @Nested
    @DisplayName("deactivate 메서드 테스트")
    inner class DeactivateTest {

        @Test
        fun `관리자는 예제를 비활성화할 수 있다`() {
            // given
            val userId = 1L
            val admin = UserFixture.create {
                id = userId
                name = "관리자"
                type = User.Type.ADMIN
            }
            val example = ExampleFixture.create {
                name = "테스트"
                this.userId = userId
                status = Example.Status.ACTIVE
            }

            // when
            example.deactivate(admin)

            // then
            assertThat(example.status).isEqualTo(Example.Status.INACTIVE)
        }

        @Test
        fun `관리자가 아닌 사용자는 예제를 비활성화할 수 없다`() {
            // given
            val ownerId = 1L
            val nonAdmin = UserFixture.create {
                id = ownerId
                name = "일반 사용자"
                type = User.Type.NORMAL
            }
            val example = ExampleFixture.create {
                name = "테스트"
                userId = ownerId
                status = Example.Status.ACTIVE
            }

            // when & then
            assertThatThrownBy { example.deactivate(nonAdmin) }
                .isInstanceOf(DomainException::class.java)
                .hasMessage("권한이 없습니다")
        }

        @Test
        fun `다른 사용자의 예제는 비활성화할 수 없다`() {
            // given
            val admin = UserFixture.create {
                id = 999L
                name = "관리자"
                type = User.Type.ADMIN
            }
            val example = ExampleFixture.create {
                name = "테스트"
                userId = 1L
                status = Example.Status.ACTIVE
            }

            // when & then
            assertThatThrownBy { example.deactivate(admin) }
                .isInstanceOf(DomainException::class.java)
                .hasMessage("권한이 없습니다")
        }
    }

}
```

**핵심 패턴**:
- **@Nested + inner class**: 기능별 테스트 그룹화
- **비즈니스 로직 메서드**: 복잡한 로직이 포함된 메서드만 테스트
- **Given-When-Then**: 명확한 테스트 구조
- **AssertJ assertions**: `assertThat()`, `assertThatThrownBy()`
- **Fixture 활용**: `ExampleFixture.create { ... }`, `UserFixture.create { ... }` DSL 패턴 사용
- **IntegrationTestContext 없음**: 엔티티 단독 테스트 (DB 불필요)

**작성 가이드**:
1. **비즈니스 로직 메서드**: if문 분기, 권한 체크, 계산 로직이 포함된 메서드만 테스트
2. **권한 체크**: 권한 검증이 있는 메서드는 성공/실패 케이스 모두 테스트
3. **단순 메서드 제외**: 단순 setter, 단순 상태 변경은 테스트 불필요
4. **Fixture 사용**: 엔티티 생성시 직접 생성자 호출 대신 Fixture 사용

**테스트 작성 대상**:
- ✅ 복잡한 비즈니스 로직 (if문 분기, 권한 체크, 계산 로직)
- ❌ guard() 검증 로직
- ❌ 단순 setter 메서드
- ❌ 단순 상태 전이 메서드

---

## 템플릿 11A: Writer Test ({Domain}WriterTest.kt)

```kotlin
package org.balanceeat.domain.example

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ExampleWriterTest : IntegrationTestContext() {

    @Autowired
    private lateinit var exampleWriter: ExampleWriter

    @Autowired
    private lateinit var exampleRepository: ExampleRepository

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {

        @Test
        fun `예제를 생성할 수 있다`() {
            // given
            val command = exampleCreateCommandFixture()

            // when
            val result = exampleWriter.create(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }

        @Test
        fun `중복된 이름으로 예제를 생성하면 예외가 발생한다`() {
            // given
            val existingExample = exampleRepository.save(
                exampleFixture {
                    name = "중복 이름"
                    userId = 1L
                }
            )

            val command = exampleCreateCommandFixture {
                name = "중복 이름"
                userId = 1L
            }

            // when & then
            assertThatThrownBy { exampleWriter.create(command) }
                .isInstanceOf(DomainException::class.java)
                .hasMessage("이미 존재하는 이름입니다")
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {

        @Test
        fun `예제를 수정할 수 있다`() {
            // given
            val example = exampleRepository.save(
                exampleFixture {
                    name = "수정 전 이름"
                    status = Example.Status.ACTIVE
                    description = "수정 전 설명"
                }
            )

            val command = exampleUpdateCommandFixture {
                id = example.id
                name = "수정 후 이름"
                status = Example.Status.INACTIVE
                description = "수정 후 설명"
            }

            // when
            val result = exampleWriter.update(command)

            // then
            assertThat(result.name).isEqualTo("수정 후 이름")
            assertThat(result.status).isEqualTo(Example.Status.INACTIVE)
            assertThat(result.description).isEqualTo("수정 후 설명")
        }
    }

    @Nested
    @DisplayName("삭제 테스트")
    inner class DeleteTest {

        @Test
        fun `예제를 삭제할 수 있다`() {
            // given
            val example = exampleRepository.save(exampleFixture())

            // when
            exampleWriter.delete(example.id)

            // then
            assertThat(exampleRepository.findById(example.id)).isEmpty
        }
    }
}
```

**핵심 패턴**:
- **@Nested + inner class**: 기능별로 테스트 그룹화 (Create, Update, Delete)
- **Given-When-Then**: 명확한 테스트 구조
- **AssertJ assertions**: `assertThat()`, `assertThatThrownBy()`
- **Fixture 활용**: DSL 스타일 픽스처 함수 사용
- **IntegrationTestContext**: 통합 테스트 환경

---

## 템플릿 11B: Reader Test ({Domain}ReaderTest.kt)

```kotlin
package org.balanceeat.domain.example

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ExampleReaderTest : IntegrationTestContext() {

    @Autowired
    private lateinit var exampleReader: ExampleReader

    @Autowired
    private lateinit var exampleRepository: ExampleRepository

    @Nested
    @DisplayName("단건 조회 테스트")
    inner class FindByIdTest {

        @Test
        fun `ID로 예제를 조회할 수 있다`() {
            // given
            val example = exampleRepository.save(exampleFixture())

            // when
            val result = exampleReader.findById(example.id)

            // then
            assertThat(result.id).isEqualTo(example.id)
            assertThat(result.name).isEqualTo(example.name)
        }

        @Test
        fun `존재하지 않는 ID로 조회시 예외가 발생한다`() {
            // given
            val nonExistentId = 999L

            // when & then
            assertThatThrownBy { exampleReader.findById(nonExistentId) }
                .isInstanceOf(EntityNotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("목록 조회 테스트")
    inner class FindByUserIdTest {

        @Test
        fun `사용자 ID로 예제 목록을 조회할 수 있다`() {
            // given
            val userId = 1L
            val example1 = exampleRepository.save(exampleFixture { this.userId = userId })
            val example2 = exampleRepository.save(exampleFixture { this.userId = userId })

            // when
            val results = exampleReader.findByUserId(userId)

            // then
            assertThat(results).hasSize(2)
            assertThat(results.map { it.id }).containsExactlyInAnyOrder(example1.id, example2.id)
        }
    }

    @Nested
    @DisplayName("존재 여부 확인 테스트")
    inner class ExistsByTest {

        @Test
        fun `사용자 ID와 이름으로 존재 여부를 확인할 수 있다`() {
            // given
            val userId = 1L
            val name = "테스트 이름"
            exampleRepository.save(exampleFixture {
                this.userId = userId
                this.name = name
            })

            // when
            val exists = exampleReader.existsByUserIdAndName(userId, name)

            // then
            assertThat(exists).isTrue()
        }

        @Test
        fun `존재하지 않는 경우 false를 반환한다`() {
            // when
            val exists = exampleReader.existsByUserIdAndName(999L, "존재하지 않는 이름")

            // then
            assertThat(exists).isFalse()
        }
    }
}
```

**핵심 패턴**:
- **@Nested + inner class**: 기능별로 테스트 그룹화 (FindById, FindByUserId, ExistsBy)
- **Given-When-Then**: 명확한 테스트 구조
- **AssertJ assertions**: `assertThat()`, `assertThatThrownBy()`
- **Fixture 활용**: DSL 스타일 픽스처 함수 사용
- **IntegrationTestContext**: 통합 테스트 환경

---

## 템플릿 11: Domain Service Test ({Domain}DomainServiceTest.kt) [레거시]

> ⚠️ **참고**: 이 템플릿은 기존 DomainService 패턴을 위한 것입니다. 신규 개발 시에는 Writer/Reader 테스트 템플릿을 사용하세요.

```kotlin
package org.balanceeat.domain.example

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired

class ExampleDomainServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var exampleDomainService: ExampleDomainService

    @Autowired
    private lateinit var exampleRepository: ExampleRepository

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {

        @Test
        fun `예제를 생성할 수 있다`() {
            // given
            val command = exampleCreateCommandFixture()

            // when
            val result = exampleDomainService.create(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }

        @Test
        fun `중복된 이름으로 예제를 생성하면 예외가 발생한다`() {
            // given
            val existingExample = exampleRepository.save(
                ExampleFixture(
                    name = "중복 이름",
                    userId = 1L
                ).create()
            )

            val command = exampleCreateCommandFixture {
                name = "중복 이름"
                userId = 1L
            }

            // when & then
            assertThatThrownBy { exampleDomainService.create(command) }
                .isInstanceOf(DomainException::class.java)
                .hasMessage("이미 존재하는 이름입니다")
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {

        @Test
        fun `예제를 수정할 수 있다`() {
            // given
            val example = exampleRepository.save(
                ExampleFixture(
                    name = "수정 전 이름",
                    status = Example.Status.ACTIVE,
                    description = "수정 전 설명"
                ).create()
            )

            val command = exampleUpdateCommandFixture {
                id = example.id
                name = "수정 후 이름"
                status = Example.Status.INACTIVE
                description = "수정 후 설명"
            }

            // when
            val result = exampleDomainService.update(command)

            // then
            assertThat(result.name).isEqualTo("수정 후 이름")
            assertThat(result.status).isEqualTo(Example.Status.INACTIVE)
            assertThat(result.description).isEqualTo("수정 후 설명")
        }

        @Test
        fun `일부 필드만 수정할 수 있다`() {
            // given
            val example = exampleRepository.save(ExampleFixture().create())

            val command = exampleUpdateCommandFixture {
                id = example.id
                name = "새로운 이름만 변경"
                status = example.status
                description = example.description
            }

            // when
            val result = exampleDomainService.update(command)

            // then
            assertThat(result.name).isEqualTo("새로운 이름만 변경")
            assertThat(result.status).isEqualTo(example.status)
            assertThat(result.description).isEqualTo(example.description)
        }
    }

    @Nested
    @DisplayName("삭제 테스트")
    inner class DeleteTest {

        @Test
        fun `예제를 삭제할 수 있다`() {
            // given
            val example = exampleRepository.save(ExampleFixture().create())

            // when
            exampleDomainService.delete(example.id)

            // then
            assertThat(exampleRepository.findById(example.id)).isEmpty
        }
    }
}
```

**핵심 패턴**:
- **@Nested + inner class**: 기능별로 테스트 그룹화
- **Given-When-Then**: 명확한 테스트 구조
- **AssertJ assertions**: `assertThat()`, `usingRecursiveComparison()`, `assertThatThrownBy()`
- **@ParameterizedTest + @CsvSource**: 반복적인 상태 변경 테스트를 파라미터화
- **비즈니스 로직 중심**: 성공 케이스와 비즈니스 요구사항 검증
- **한글 테스트명**: 백틱(`)을 사용한 자연어 설명
- **Fixture 활용**:
  - Entity Fixture: `ExampleFixture().create()`
  - Command Fixture: `exampleCreateCommandFixture { }`, `exampleUpdateCommandFixture { }`
  - 람다 블록 내에서 `this` 키워드 사용 금지 (변수 섀도잉 방지)

**작성 가이드**:
1. **성공 케이스 우선**: 각 기능의 정상 동작을 먼저 테스트
2. **비즈니스 요구사항**: if문 분기, 계산 로직, 상태 전이 등 도메인 규칙 검증
3. **일부 필드 수정**: 선택적 필드 업데이트 케이스 포함
4. **ParameterizedTest**: 반복적인 패턴(상태 변경 등)은 파라미터화
5. **예외 케이스**: 비즈니스 규칙 위반 시나리오만 포함 (중복 생성 등)
6. **엔티티 없음 테스트 지양**: 단순 Not Found 예외는 비즈니스 가치 없음
7. **Fixture 사용시 주의사항**:
   - Command Fixture는 DSL 스타일 함수 사용 (`exampleCreateCommandFixture { }`)
   - 람다 블록 내에서 `this` 키워드 사용 금지 (변수 섀도잉 방지)
   - 외부 변수와 동일한 이름의 속성 설정 시 변수명 변경 필요

---

## 개발 체크리스트

### 새로운 도메인 추가시 (Writer/Reader 패턴)

- [ ] **패키지 구조**: `org.balanceeat.domain.{domain}` 생성
- [ ] **엔티티**: `{Domain}.kt` 작성, `BaseEntity` 상속, `guard()` 구현
- [ ] **Command**: `{Domain}Command.kt` 작성 (Create, Update, Delete)
- [ ] **Query** (필요시): `{Domain}Query.kt` 작성 (Search 등 복잡한 조회 조건)
- [ ] **Result**: `{Domain}Result.kt` 작성
  - `{Domain}Result`: 기본 조회 결과
  - `{Domain}SearchResult`: Querydsl 검색 결과 (with `@QueryProjection`)
  - `{Domain}Details`: 상세 정보 (필요시)
  - `{Domain}Summary`: 목록용 간략 정보 (필요시)
- [ ] **Repository**: `{Domain}Repository.kt` 작성
- [ ] **Querydsl** (필요시): Custom/Impl 작성
- [ ] **Writer**: `{Domain}Writer.kt` 작성, `@Component` 적용 (CUD 담당)
- [ ] **Reader**: `{Domain}Reader.kt` 작성, `@Component` 적용 (조회 담당)
- [ ] **Fixture**: `{Domain}Fixture.kt` + `{Domain}CommandFixture.kt` 작성
- [ ] **테스트**: `{Domain}WriterTest.kt` + `{Domain}ReaderTest.kt` 작성
- [ ] **예외 처리**: `DomainStatus`에 도메인별 예외 상태 추가

### 새로운 도메인 추가시 (레거시 DomainService 패턴)

> ⚠️ **참고**: 기존 코드와의 일관성을 위해 DomainService 패턴을 사용하는 경우

- [ ] **패키지 구조**: `org.balanceeat.domain.{domain}` 생성
- [ ] **엔티티**: `{Domain}.kt` 작성, `BaseEntity` 상속, `guard()` 구현
- [ ] **Command**: `{Domain}Command.kt` 작성 (Create, Update, Delete)
- [ ] **Query** (필요시): `{Domain}Query.kt` 작성 (Search 등 복잡한 조회 조건)
- [ ] **Result**: `{Domain}Result.kt` 작성
- [ ] **Repository**: `{Domain}Repository.kt` 작성
- [ ] **Querydsl** (필요시): Custom/Impl 작성
- [ ] **Domain Service**: `{Domain}DomainService.kt` 작성, `@DomainService` 적용
- [ ] **Fixture**: `{Domain}Fixture.kt` + `{Domain}CommandFixture.kt` 작성
- [ ] **테스트**: `{Domain}DomainServiceTest.kt` 작성
- [ ] **예외 처리**: `DomainStatus`에 도메인별 예외 상태 추가

### 코드 품질 체크

- [ ] 주석 없이 코드 작성
- [ ] `guard()` 메서드에 모든 검증 로직 구현
- [ ] Enum은 `EnumType.STRING` 매핑
- [ ] 상수는 companion object에 정의
- [ ] 한글 에러 메시지 작성
- [ ] 테스트 커버리지 확인

---

## 명명 규칙 요약

### Writer/Reader 패턴 (신규)

| 항목 | 규칙 | 예시 |
|-----|------|-----|
| Entity | `{Domain}.kt` | `User.kt`, `Diet.kt` |
| Command | `{Domain}Command.kt` | `UserCommand.kt` |
| Query | `{Domain}Query.kt` | `FoodQuery.kt` (필요시) |
| Result | `{Domain}Result.kt` | `FoodResult.kt` |
| Writer | `{Domain}Writer.kt` | `UserWriter.kt` |
| Reader | `{Domain}Reader.kt` | `UserReader.kt` |
| Repository | `{Domain}Repository.kt` | `UserRepository.kt` |
| Fixture | `{Domain}Fixture.kt` | `UserFixture.kt` |
| Command Fixture | `{Domain}CommandFixture.kt` | `UserCommandFixture.kt` |
| Writer Test | `{Domain}WriterTest.kt` | `UserWriterTest.kt` |
| Reader Test | `{Domain}ReaderTest.kt` | `UserReaderTest.kt` |

### DomainService 패턴 (레거시)

| 항목 | 규칙 | 예시 |
|-----|------|-----|
| Entity | `{Domain}.kt` | `User.kt`, `Diet.kt` |
| Command | `{Domain}Command.kt` | `UserCommand.kt` |
| Query | `{Domain}Query.kt` | `FoodQuery.kt` (필요시) |
| Result | `{Domain}Result.kt` | `FoodResult.kt` |
| Repository | `{Domain}Repository.kt` | `UserRepository.kt` |
| Domain Service | `{Domain}DomainService.kt` | `UserDomainService.kt` |
| Fixture | `{Domain}Fixture.kt` | `UserFixture.kt` |
| Command Fixture | `{Domain}CommandFixture.kt` | `UserCommandFixture.kt` |
| Test | `{Domain}DomainServiceTest.kt` | `UserDomainServiceTest.kt` |

---

## 참고 사항

### 기존 프로젝트와의 차이점

Balance-Eat 프로젝트는 다음과 같은 차이점이 있습니다:

1. **BaseEntity**: `LocalDateTime` 사용 (ZonedDateTime 대신)
2. **NEW_ID**: `0L` 상수로 신규 엔티티 표시
3. **Package**: `org.balanceeat.domain.{domain}` 구조
4. **DomainService**: `@Service` 어노테이션 사용 (별도 `@DomainService` 없음)
5. **Test**: Kotest assertions + JUnit 5 조합
6. **Fixture**: `TestFixture` 인터페이스 및 생성 함수 활용

### Command vs Query vs Result 분리

Balance-Eat 프로젝트는 CQRS 패턴을 부분적으로 적용합니다:

**Command (CUD)**:
- `{Domain}Command.kt`: Create, Update, Delete 명령
- 상태 변경을 목적으로 함
- 예: `UserCommand.Create`, `FoodCommand.Update`

**Query (조회 조건)**:
- `{Domain}Query.kt`: 복잡한 조회 조건 (필요시만 작성)
- 상태 변경 없이 조회만 수행
- 예: `FoodQuery.Search` (검색), `DietQuery.Filter` (필터링)

**Result (조회 결과)**:
- `{Domain}Result.kt`: 조회 결과 객체 모음
- Entity → Result 변환으로 도메인 계층 결과 전달
- **Result 타입 구분**:
  - `{Domain}Result`: Domain Service의 기본 반환 타입 (CUD 작업 결과)
  - `{Domain}SearchResult`: Querydsl 검색 결과 (`@QueryProjection`)
  - `{Domain}Details`: 상세 정보 조회 (연관 데이터 포함)
  - `{Domain}Summary`: 목록 조회용 간략 정보
- 예: `FoodResult`, `FoodSearchResult`, `FoodDetails`, `FoodSummary`

### Writer/Reader 패턴 전환 가이드

#### 전환 시점 결정

**권장 전환 시점**:
1. **신규 도메인**: 처음부터 Writer/Reader 패턴 적용
2. **대규모 리팩토링 시**: 도메인 전체를 재작성하는 경우
3. **명확한 책임 분리 필요 시**: CUD와 Read 로직이 복잡하게 얽힌 경우

**전환 보류 권장**:
1. **안정된 도메인**: 잘 동작하는 기존 DomainService
2. **단순 CRUD**: 복잡도가 낮은 도메인
3. **리소스 부족**: 전환 작업 인력/시간이 부족한 경우

#### 단계별 전환 프로세스

**1단계: Writer 분리**
```kotlin
// Before: DomainService
@DomainService
class UserDomainService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(command: UserCommand.Create): UserResult { ... }

    @Transactional
    fun update(command: UserCommand.Update): UserResult { ... }

    @Transactional
    fun delete(id: Long) { ... }

    @Transactional(readOnly = true)
    fun findById(id: Long): UserResult { ... }
}

// After: Writer
@Component
class UserWriter(
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(command: UserCommand.Create): UserResult { ... }

    @Transactional
    fun update(command: UserCommand.Update): UserResult { ... }

    @Transactional
    fun delete(id: Long) { ... }
}
```

**2단계: Reader 분리 (선택적)**
```kotlin
// Option B: Reader 래퍼 메서드 제공
@Component
class UserReader(
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun findById(id: Long): UserResult {
        return userRepository.findByIdOrNull(id)
            ?.let { UserResult.from(it) }
            ?: throw EntityNotFoundException(DomainStatus.USER_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findByEmail(email: String): UserResult? {
        return userRepository.findByEmail(email)
            ?.let { UserResult.from(it) }
    }
}

// Option C: Repository 직접 사용 허용 (단순 조회)
// Reader는 복잡한 비즈니스 로직이 포함된 조회만 담당
```

**3단계: 상위 계층 (Application Service) 수정**
```kotlin
// Before
@Service
class UserApplicationService(
    private val userDomainService: UserDomainService
) {
    fun register(request: RegisterRequest): UserResponse {
        val command = request.toCommand()
        val result = userDomainService.create(command)
        return UserResponse.from(result)
    }
}

// After
@Service
class UserApplicationService(
    private val userWriter: UserWriter,
    private val userReader: UserReader // 또는 userRepository 직접 사용
) {
    fun register(request: RegisterRequest): UserResponse {
        val command = request.toCommand()
        val result = userWriter.create(command)
        return UserResponse.from(result)
    }
}
```

**4단계: 테스트 수정**
```kotlin
// Before
class UserDomainServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var userDomainService: UserDomainService
    // ...
}

// After
class UserWriterTest : IntegrationTestContext() {
    @Autowired
    private lateinit var userWriter: UserWriter
    // ...
}

class UserReaderTest : IntegrationTestContext() {
    @Autowired
    private lateinit var userReader: UserReader
    // ...
}
```

#### 전환 체크리스트

- [ ] Writer 클래스 생성 및 CUD 메서드 이동
- [ ] Reader 클래스 생성 (Option B 선택 시)
- [ ] 기존 DomainService 제거 또는 Deprecated 표시
- [ ] Application Service 의존성 변경
- [ ] Controller 영향도 확인 및 수정
- [ ] Writer/Reader 테스트 작성
- [ ] 기존 DomainService 테스트 제거
- [ ] 통합 테스트 실행 및 검증
- [ ] 문서 업데이트

### 실제 도메인 예시

**현재 프로젝트 구조** (DomainService 패턴):
- **User**: 사용자 도메인 (복잡한 검증 로직)
- **Diet**: 식단 도메인 (연관관계, Querydsl)
- **Food**: 음식 도메인 (Query/Result 분리 예시)
  - `FoodCommand.kt`: Create, Update
  - `FoodQuery.kt`: Search
  - `FoodResult.kt`: FoodResult, FoodSearchResult

**신규 도메인 개발 시**: Writer/Reader 패턴을 적용하여 명확한 책임 분리를 구현하세요.

---

이 템플릿을 따라 개발하면 일관성 있고 유지보수가 용이한 도메인 레이어를 구축할 수 있습니다.
