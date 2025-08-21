# Entity Guidelines

## 개요

Balance-Eat 프로젝트의 JPA 엔티티 설계 및 구현을 위한 표준 가이드라인입니다.

## 엔티티 기본 구조

### 1. 클래스 선언

```kotlin
@Entity
@Table(name = "\"table_name\"")
class EntityName(
    // 필드 정의
) : BaseEntity() {
    // 검증 로직 및 중첩 클래스
}
```

### 2. 기본 원칙

- **상속**: 모든 엔티티는 `BaseEntity`를 상속
- **ID 필드**: `val id: Long = NEW_ID`로 선언
- **테이블명**: 예약어는 쌍따옴표로 감싸기 (`"user"`)
- **검증**: `guard()` 메서드로 비즈니스 규칙 검증

## 필드 설계 패턴

### 1. ID 필드
```kotlin
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
val id: Long = NEW_ID
```

### 2. 필수 필드
```kotlin
@Column(nullable = false)
var requiredField: String
```

### 3. 선택적 필드
```kotlin
@Column(nullable = true)
var optionalField: String? = null
```

### 4. 유니크 필드
```kotlin
@Column(nullable = false, unique = true, length = 36)
val uuid: String
```

### 5. 컬럼명 매핑
```kotlin
@Column(name = "activity_level", length = 20)
var activityLevel: ActivityLevel? = null
```

### 6. Enum 필드
```kotlin
@Enumerated(EnumType.STRING)
@Column(length = 10)
var gender: Gender
```

## 데이터 타입별 제약사항

### 1. 문자열 필드
- **길이 제한**: `@Column(length = N)` 명시
- **유효성 검증**: `guard()` 메서드에서 길이 및 형식 검증
- **이메일**: 정규식으로 형식 검증

```kotlin
@Column(nullable = true, length = 255)
var email: String? = null

// guard() 메서드 내
email?.let {
    require(it.length <= 255) { "이메일은 255자를 초과할 수 없습니다" }
    require(it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
        "유효한 이메일 형식이 아닙니다"
    }
}
```

### 2. 숫자 필드

#### 정수형
```kotlin
@Column(nullable = false)
var age: Int

// 범위 검증
require(age in 1..150) { "나이는 1세 이상 150세 이하여야 합니다" }
```

#### 실수형
```kotlin
@Column(nullable = false)
var weight: Double

// 범위 및 소수점 검증
require(weight in 10.0..1000.0) { "몸무게는 10kg 이상 1000kg 이하여야 합니다" }
require(weight.toString().substringAfter(".").length <= 2) { "몸무게는 소수점 2자리까지만 입력 가능합니다" }
```

### 3. Enum 타입

#### 단순 Enum
```kotlin
enum class Gender {
    MALE,
    FEMALE,
    OTHER,
}
```

#### 값을 갖는 Enum
```kotlin
enum class ActivityLevel(val factor: Double) {
    SEDENTARY(1.2),    // 거의 활동 안 함
    LIGHT(1.375),      // 가벼운 활동 (주 1~3회)
    MODERATE(1.55),    // 보통 활동 (주 3~5회)
    ACTIVE(1.725)      // 활발한 활동 (매일 격렬)
}
```

## 검증 로직 (guard 메서드)

### 1. 기본 구조
```kotlin
override fun guard() {
    // 필수 필드 검증
    // 형식 검증
    // 범위 검증
    // 비즈니스 규칙 검증
}
```

### 2. 검증 패턴

#### 길이 검증
```kotlin
require(name.length <= 100) { "이름은 100자를 초과할 수 없습니다" }
```

#### 빈 값 검증
```kotlin
require(uuid.isNotBlank()) { "UUID는 필수값입니다" }
```

#### 범위 검증
```kotlin
require(age in 1..150) { "나이는 1세 이상 150세 이하여야 합니다" }
require(weight in 10.0..1000.0) { "몸무게는 10kg 이상 1000kg 이하여야 합니다" }
```

#### 선택적 필드 검증
```kotlin
optionalField?.let {
    require(it.isValid()) { "검증 실패 메시지" }
}
```

#### 소수점 자릿수 검증
```kotlin
require(value.toString().substringAfter(".").length <= 2) { "소수점 2자리까지만 입력 가능합니다" }
```

## 네이밍 컨벤션

### 1. 클래스명
- **패스칼 케이스**: `User`, `FoodItem`, `MealPlan`
- **도메인 기반**: 비즈니스 도메인을 명확히 표현

### 2. 필드명
- **카멜 케이스**: `firstName`, `activityLevel`, `targetWeight`
- **의미 명확성**: 필드의 용도를 명확히 표현

### 3. 테이블/컬럼명
- **스네이크 케이스**: `activity_level`, `target_weight`, `fat_percentage`
- **예약어 처리**: `"user"`, `"order"` 등은 쌍따옴표로 감싸기

### 4. Enum 값
- **대문자 스네이크 케이스**: `MALE`, `FEMALE`, `SEDENTARY`, `LIGHT`

## 모범 사례

### 1. 필드 가시성
- **ID**: `val` (불변)
- **UUID**: `val` (불변)
- **비즈니스 필드**: `var` (가변)

### 2. 기본값 설정
```kotlin
var optionalField: String? = null
var booleanField: Boolean = false
var numericField: Int = 0
```

### 3. Provider 정보 (OAuth/소셜 로그인)
```kotlin
@Column(name = "provider_id", length = 255)
var providerId: String? = null

@Column(name = "provider_type", length = 50)
var providerType: String? = null
```

### 4. 목표 값 필드
- **접두사**: `target` 사용 (`targetWeight`, `targetCalorie`)
- **선택적**: 모든 목표 값은 nullable

## 안티 패턴

### ❌ 피해야 할 것들

1. **검증 없는 필드**
```kotlin
var weight: Double  // 범위 검증 없음
```

2. **의미 없는 네이밍**
```kotlin
var data: String    // 용도 불명확
var value: Double   // 무엇의 값인지 불분명
```

3. **하드코딩된 제약**
```kotlin
require(age < 200)  // 매직 넘버 사용
```

4. **일관성 없는 null 처리**
```kotlin
var email: String?  // null 허용하지만 검증 없음
```

## 체크리스트

### ✅ 새 엔티티 생성 시 확인사항

- [ ] `BaseEntity` 상속
- [ ] `@Entity`, `@Table` 어노테이션 추가
- [ ] ID 필드 올바르게 설정
- [ ] 모든 필드에 적절한 `@Column` 설정
- [ ] `guard()` 메서드에서 모든 필드 검증
- [ ] Enum 타입 적절히 활용
- [ ] 네이밍 컨벤션 준수
- [ ] 에러 메시지 한국어로 작성
- [ ] 비즈니스 규칙 반영
- [ ] 소수점 자릿수 제한 (필요시)
- [ ] **테스트 픽스처 생성**: `src/testFixtures/kotlin/org/balanceeat/domain/{domain}/`에 `{Entity}Fixture.kt` 생성

### ✅ 기존 엔티티 수정 시 확인사항

- [ ] 기존 검증 로직과 일관성 유지
- [ ] 새 필드의 기본값 설정
- [ ] 마이그레이션 스크립트 필요성 검토
- [ ] 관련 테스트 케이스 업데이트
- [ ] **픽스처 업데이트**: 엔티티 변경사항을 픽스처에 반영

## 테스트 픽스처 연동

### 픽스처 생성 규칙
모든 엔티티 생성 시 반드시 해당하는 테스트 픽스처를 생성해야 합니다.

#### 픽스처 위치
```
src/testFixtures/kotlin/org/balanceeat/domain/{domain}/{Entity}Fixture.kt
```

#### 픽스처 패턴
```kotlin
class UserFixture(
    var id: Long = NEW_ID,
    var name: String = "테스트 사용자",
    var uuid: String = UUID.randomUUID().toString(),
    var email: String? = "test@example.com",
    var gender: User.Gender = User.Gender.MALE,
    var age: Int = 30,
    var weight: Double = 70.0,
    var height: Double = 175.0,
    // ... 모든 엔티티 필드
) : TestFixture<User> {
    override fun create(): User {
        return User(
            id = id,
            name = name,
            uuid = uuid,
            email = email,
            gender = gender,
            age = age,
            weight = weight,
            height = height,
            // ... 모든 필드 매핑
        )
    }
}
```

자세한 픽스처 가이드라인은 [claude/fixture-guidelines.md](fixture-guidelines.md)를 참고하세요.