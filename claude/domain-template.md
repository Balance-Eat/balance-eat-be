# Balance-Eat Domain Layer 표준 템플릿

## 필수 규칙

- **주석 금지**: 코드 내 주석은 작성하지 않습니다
- **검증 로직**: 모든 엔티티는 `guard()` 메서드에서 비즈니스 규칙 검증을 수행합니다

## 목적

Balance-Eat 프로젝트의 도메인 레이어 표준 템플릿을 제공합니다. 각 도메인은 독립적인 패키지 구조를 가지며, Kotlin + Spring Data JPA + Querydsl을 기반으로 합니다.

---

## 표준 패키지 구조

```
domain/src/main/kotlin/org/balanceeat/domain/{domain}/
├── {Domain}.kt                      # 엔티티
├── {Domain}Command.kt                # Command 객체
├── {Domain}Dto.kt                    # DTO 객체
├── {Domain}DomainService.kt          # 도메인 서비스
├── {Domain}Repository.kt             # Repository 인터페이스
├── {Domain}RepositoryCustom.kt       # Querydsl 커스텀 인터페이스 (필요시)
└── {Domain}RepositoryCustomImpl.kt   # Querydsl 구현 (필요시)

domain/src/testFixtures/kotlin/org/balanceeat/domain/{domain}/
├── {Domain}Fixture.kt                # 엔티티 픽스처
└── {Domain}CommandFixture.kt         # Command 픽스처

domain/src/test/kotlin/org/balanceeat/domain/{domain}/
├── {Domain}Test.kt                   # 엔티티 테스트 (필요시)
└── {Domain}DomainServiceTest.kt      # 도메인 서비스 테스트
```

**파일 설명**:
- **{Domain}.kt**: JPA 엔티티 및 Enum 타입 정의
- **{Domain}Command.kt**: Create, Update 등 명령 객체
- **{Domain}Dto.kt**: 조회용 DTO 객체
- **{Domain}DomainService.kt**: 비즈니스 로직 처리 CUD 담당 (@DomainService) 
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

sealed class ExampleCommand {

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
- sealed class 사용
- Create/Update/Delete 등 data class로 정의
- 필수값과 선택값 구분

---

## 템플릿 3: DTO ({Domain}Dto.kt)

```kotlin
package org.balanceeat.domain.example

import java.time.LocalDateTime

class ExampleDto {

    data class Details(
        val id: Long,
        val name: String,
        val status: Example.Status,
        val userId: Long,
        val description: String?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    ) {
        companion object {
            fun from(example: Example): Details {
                return Info(
                    id = example.id,
                    name = example.name,
                    status = example.status,
                    userId = example.userId,
                    description = example.description,
                    createdAt = example.createdAt,
                    updatedAt = example.updatedAt
                )
            }
        }
    }

    data class Summary(
        val id: Long,
        val name: String,
        val status: Example.Status
    ) {
        companion object {
            fun from(example: Example): Summary {
                return Summary(
                    id = example.id,
                    name = example.name,
                    status = example.status
                )
            }
        }
    }
}
```

**핵심 패턴**:
- Details: 상세 정보용 DTO
- Summary: 목록 조회용 간략 DTO
- companion object의 `from()` 팩토리 메서드

---

## 템플릿 4: Repository ({Domain}Repository.kt)

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

## 템플릿 5: Repository Custom (Querydsl)

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

## 템플릿 6: Domain Service ({Domain}DomainService.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ExampleDomainService(
    private val exampleRepository: ExampleRepository
) {

    @Transactional
    fun create(command: ExampleCommand.Create): Example {
        validateDuplicateName(command.userId, command.name)

        val example = Example(
            name = command.name,
            userId = command.userId,
            status = command.status,
            description = command.description
        )

        return exampleRepository.save(example)
    }

    @Transactional
    fun update(command: ExampleCommand.Update): Example {
        val example = getById(command.id)

        example.updateName(command.name)
        example.updateStatus(command.status)
        command.description?.let { example.description = it }

        return example
    }

    @Transactional
    fun delete(id: Long) {
        val example = getById(id)
        exampleRepository.delete(example)
    }

    fun getById(id: Long): Example {
        return exampleRepository.findByIdOrNull(id)
            ?: throw DomainException(DomainStatus.EXAMPLE_NOT_FOUND)
    }

    fun getAllByUserId(userId: Long): List<Example> {
        return exampleRepository.findByUserId(userId)
    }

    private fun validateDuplicateName(userId: Long, name: String) {
        if (exampleRepository.existsByUserIdAndName(userId, name)) {
            throw DomainException(DomainStatus.EXAMPLE_ALREADY_EXISTS)
        }
    }
}
```

**핵심 패턴**:
- `@Service` 어노테이션
- `@Transactional(readOnly = true)` 클래스 레벨 적용
- 쓰기 메서드에만 `@Transactional` 오버라이드
- 검증 로직은 private 메서드로 분리
- `findByIdOrNull()` + Elvis 연산자로 예외 처리

---

## 템플릿 7: Test Fixture ({Domain}Fixture.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.config.FixtureReflectionUtils
import org.balanceeat.domain.config.TestFixture

class ExampleFixture : TestFixture {

    override fun clear() {
        id = 1L
        name = "테스트 예제"
        status = Example.Status.ACTIVE
        userId = 1L
        description = null
    }

    var id: Long = 1L
    var name: String = "테스트 예제"
    var status: Example.Status = Example.Status.ACTIVE
    var userId: Long = 1L
    var description: String? = null

    fun build(): Example {
        val example = Example(
            id = 0L,
            name = name,
            status = status,
            userId = userId,
            description = description
        )
        FixtureReflectionUtils.setId(example, id)
        return example
    }

    companion object {
        fun create(fixture: ExampleFixture.() -> Unit = {}): Example {
            val exampleFixture = ExampleFixture()
            exampleFixture.clear()
            exampleFixture.fixture()
            return exampleFixture.build()
        }
    }
}
```

**핵심 패턴**:
- `TestFixture` 인터페이스 구현
- `clear()`: 기본값 초기화
- `build()`: 엔티티 생성 + ID 설정
- companion object `create()`: DSL 스타일 픽스처 생성

---

## 템플릿 8: Command Fixture ({Domain}CommandFixture.kt)

```kotlin
package org.balanceeat.domain.example

import org.balanceeat.domain.config.TestFixture

class ExampleCommandFixture {

    class Create(
        var name: String = "테스트 예제",
        var userId: Long = 1L,
        var status: Example.Status = Example.Status.ACTIVE,
        var description: String? = null
    ): TestFixture {
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
    ): TestFixture {
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
```

**핵심 패턴**:
- Command별 Fixture 클래스 중첩
- 각 Fixture는 `TestFixture` 구현

---

## 템플릿 9: Entity Test ({Domain}Test.kt)

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

## 템플릿 10: Domain Service Test ({Domain}DomainServiceTest.kt)

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
            val command = ExampleCommandFixture.Create().create()

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

            val command = ExampleCommandFixture.Create(
                name = "중복 이름",
                userId = 1L
            ).create()

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

            val command = ExampleCommandFixture.Update(
                id = example.id,
                name = "수정 후 이름",
                status = Example.Status.INACTIVE,
                description = "수정 후 설명"
            ).create()

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

            val command = ExampleCommandFixture.Update(
                id = example.id,
                name = "새로운 이름만 변경",
                status = example.status,
                description = example.description
            ).create()

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
- **Fixture 활용**: `ExampleFixture().create()`, `ExampleCommandFixture.Create().create()`

**작성 가이드**:
1. **성공 케이스 우선**: 각 기능의 정상 동작을 먼저 테스트
2. **비즈니스 요구사항**: if문 분기, 계산 로직, 상태 전이 등 도메인 규칙 검증
3. **일부 필드 수정**: 선택적 필드 업데이트 케이스 포함
4. **ParameterizedTest**: 반복적인 패턴(상태 변경 등)은 파라미터화
5. **예외 케이스**: 비즈니스 규칙 위반 시나리오만 포함 (중복 생성 등)
6. **엔티티 없음 테스트 지양**: 단순 Not Found 예외는 비즈니스 가치 없음

---

## 개발 체크리스트

### 새로운 도메인 추가시

- [ ] **패키지 구조**: `org.balanceeat.domain.{domain}` 생성
- [ ] **엔티티**: `{Domain}.kt` 작성, `BaseEntity` 상속, `guard()` 구현
- [ ] **Command**: `{Domain}Command.kt` 작성 (Create, Update)
- [ ] **DTO**: `{Domain}Dto.kt` 작성 (Info, Summary)
- [ ] **Repository**: `{Domain}Repository.kt` 작성
- [ ] **Querydsl** (필요시): Custom/Impl 작성
- [ ] **Domain Service**: `{Domain}DomainService.kt` 작성, `@Service` 적용
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

| 항목 | 규칙 | 예시 |
|-----|------|-----|
| Entity | `{Domain}.kt` | `User.kt`, `Diet.kt` |
| Command | `{Domain}Command.kt` | `UserCommand.kt` |
| DTO | `{Domain}Dto.kt` | `UserDto.kt` |
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
6. **Fixture**: `TestFixture` 인터페이스 + `FixtureReflectionUtils` 활용

### 실제 도메인 예시

프로젝트 내 참고 가능한 도메인:
- `User`: 사용자 도메인 (복잡한 검증 로직)
- `Diet`: 식단 도메인 (연관관계, Querydsl)
- `Food`: 음식 도메인 (단순한 구조)

이 템플릿을 따라 개발하면 일관성 있고 유지보수가 용이한 도메인 레이어를 구축할 수 있습니다.
