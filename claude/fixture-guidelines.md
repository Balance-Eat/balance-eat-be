# Test Fixture Guidelines

## Overview

All test fixtures in the Balance-Eat project follow a standardized constructor-based pattern for consistency and ease of use.

## Standard Fixture Pattern

### Basic Structure

```kotlin
class EntityFixture(
    var id: Long = NEW_ID,
    var field1: Type = defaultValue1,
    var field2: Type = defaultValue2,
    // ... all entity fields with default values
) : TestFixture<Entity> {
    override fun create(): Entity {
        return Entity(
            id = id,
            field1 = field1,
            field2 = field2,
            // ... all fields
        )
    }
}
```

### Usage Examples

```kotlin
// Create with default values
val user = UserFixture().create()

// Create with specific values
val user = UserFixture(
    name = "테스트 사용자",
    email = "test@example.com"
).create()

// Save to repository
val savedUser = userRepository.save(
    UserFixture(
        name = "특별한 사용자",
        age = 30
    ).create()
)
```

## File Organization Rules

### TestFixtures Directory Structure
- All fixtures are located in `src/testFixtures/kotlin` directory
- This uses Gradle's `java-test-fixtures` plugin for sharing fixtures across modules
- Fixtures can be used as dependencies in other modules

### One Fixture Per File
- Each fixture class should be in its own separate file
- File naming convention: `{Entity}Fixture.kt`
- Located in the same package structure as the entity being tested

### File Structure Example
```
domain/src/
├── testFixtures/kotlin/org/balanceeat/domain/  # Test fixtures (shared across modules)
│   ├── config/
│   │   └── TestFixture.kt                      # Base test fixture interface
│   ├── user/
│   │   ├── UserFixture.kt                      # User entity fixture
│   │   └── UserCommandFixture.kt               # UserCommand.Create fixture
│   ├── food/
│   │   ├── FoodFixture.kt                      # Food entity fixture
│   │   └── FoodCommandFixture.kt               # FoodCommand.Create fixture
│   └── diet/
│       ├── DietFixture.kt                      # Diet entity fixture
│       ├── DietCommandFixture.kt               # DietCommand.Create fixture
│       └── DietFoodFixture.kt                  # DietFood entity fixture
└── test/kotlin/org/balanceeat/domain/          # Test files
    ├── user/
    │   └── UserDomainServiceTest.kt            # User integration tests
    ├── food/
    │   └── FoodTest.kt                         # Food tests
    └── diet/
        └── DietDomainServiceTest.kt            # Diet integration tests
```

## Fixture Types

### 1. Entity Fixtures
For domain entities that extend `TestFixture<Entity>`:
```kotlin
class UserFixture(...) : TestFixture<User>
class FoodFixture(...) : TestFixture<Food>
class DietFixture(...) : TestFixture<Diet>
```

### 2. Command Fixtures
Command fixtures use nested class structure to group related commands:
```kotlin
class FoodCommandFixture {
    class Create(
        var uuid: String = UuidGenerator.generateUuidV7().toString(),
        var name: String = "테스트 음식",
        var perCapitaIntake: Double = 1.0,
        var unit: String = "g",
        var carbohydrates: Double = 20.0,
        var protein: Double = 5.0,
        var fat: Double = 2.0,
        var isAdminApproved: Boolean = false
    ) : TestFixture<FoodCommand.Create> {
        override fun create(): FoodCommand.Create {
            return FoodCommand.Create(
                uuid = uuid,
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

class DietCommandFixture {
    class Create(
        var foods: MutableList<DietCommand.AddFood> = mutableListOf(
            DietCommand.AddFood(
                foodId = 1L,
                actualServingSize = 1.0,
                servingUnit = "개"
            )
        )
    ) : TestFixture<DietCommand.Create> {
        override fun create(): DietCommand.Create {
            return DietCommand.Create(foods = foods)
        }
    }
}
```

## Best Practices

### 1. Default Values
- Provide sensible default values for all fields
- Use realistic test data that reflects actual usage
- For Korean text: use Korean names and descriptions
- For dates: use `LocalDate.now()` or `LocalDateTime.now()`

### 2. Constructor Parameters
- All fields should be `var` (mutable) constructor parameters
- Use the same order as the entity constructor when possible
- Include all entity fields, even optional ones

### 3. Command Fixture Usage
Command fixtures are used with the nested class pattern:

```kotlin
// Create with default values
val command = FoodCommandFixture.Create().create()

// Create with specific values
val command = FoodCommandFixture.Create(
    name = "김치찌개",
    isAdminApproved = true
).create()

// Usage in service tests
val result = foodDomainService.create(
    FoodCommandFixture.Create(
        name = "특별한 음식",
        isAdminApproved = true
    ).create()
)
```

### 4. Mutable Collections
- Use `MutableList<T>` instead of `List<T>` for collection fields
- Use `mutableListOf()`, `mutableSetOf()`, `mutableMapOf()` for default values
- This allows tests to modify collections after fixture creation

```kotlin
class DietCommandFixture {
    class Create(
        var foods: MutableList<DietCommand.AddFood> = mutableListOf(
            DietCommand.AddFood(
                foodId = 1L,
                actualServingSize = 1.0,
                servingUnit = "개"
            )
        )
    ) : TestFixture<DietCommand.Create> {
        override fun create(): DietCommand.Create {
            return DietCommand.Create(foods = foods)
        }
    }
}

// Usage in tests:
val fixture = DietCommandFixture.Create()
fixture.foods.add(DietCommand.AddFood(...)) // This is now possible
fixture.foods.clear() // This is also possible
val command = fixture.create()
```

### 5. Naming Conventions
- Fixture class: `{Entity}Fixture`
- File name: `{Entity}Fixture.kt`
- Test method names: Korean backtick format (`` `테스트 내용` ``)

### 6. Integration with Tests
```kotlin
@Test
fun `사용자를 생성할 수 있다`() {
    // given
    val user = userRepository.save(
        UserFixture(
            name = "테스트 사용자",
            email = "test@example.com"
        ).create()
    )
    
    // when & then
    assertThat(user.id).isGreaterThan(0L)
    assertThat(user.name).isEqualTo("테스트 사용자")
}
```

## File Naming Conventions
- Fixtures: `{Entity}Fixture.kt`
- Location: `src/testFixtures/kotlin/org/balanceeat/domain/{domain}/`

## Quality Standards

- **Consistency**: All fixtures follow the same constructor pattern
- **Maintainability**: Each fixture in its own file for easy maintenance  
- **Usability**: Simple to use with both default and custom values
- **Readability**: Clear naming and structure for better test comprehension

This fixture system ensures consistent, maintainable, and easy-to-use test data creation across the entire Balance-Eat project.