package org.balanceeat.domain.food

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.UuidGenerator
import java.time.LocalDateTime

class FoodResultFixture(
    var id: Long = 1L,
    var uuid: String = UuidGenerator.generateUuidV7().toString(),
    var name: String = "테스트 음식",
    var userId: Long = 1L,
    var servingSize: Double = 100.0,
    var unit: String = "g",
    var perServingCalories: Double = 159.0,
    var carbohydrates: Double = 25.0,
    var protein: Double = 8.0,
    var fat: Double = 3.0,
    var brand: String = "테스트 브랜드",
    var isAdminApproved: Boolean = false,
    var createdAt: LocalDateTime = LocalDateTime.now()
) : TestFixture<FoodResult> {
    override fun create(): FoodResult {
        return FoodResult(
            id = id,
            uuid = uuid,
            name = name,
            userId = userId,
            servingSize = servingSize,
            unit = unit,
            perServingCalories = perServingCalories,
            carbohydrates = carbohydrates,
            protein = protein,
            fat = fat,
            brand = brand,
            isAdminApproved = isAdminApproved,
            createdAt = createdAt
        )
    }
}

class FoodSearchResultFixture(
    var id: Long = 1L,
    var uuid: String = UuidGenerator.generateUuidV7().toString(),
    var name: String = "테스트 음식",
    var userId: Long = 1L,
    var servingSize: Double = 100.0,
    var unit: String = "g",
    var carbohydrates: Double = 25.0,
    var protein: Double = 8.0,
    var fat: Double = 3.0,
    var perServingCalories: Double = 159.0, // 25*4 + 8*4 + 3*9 = 159
    var brand: String = "테스트 브랜드",
    var isAdminApproved: Boolean = false,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : TestFixture<FoodSearchResult> {
    override fun create(): FoodSearchResult {
        return FoodSearchResult(
            id = id,
            uuid = uuid,
            name = name,
            userId = userId,
            servingSize = servingSize,
            unit = unit,
            perServingCalories = perServingCalories,
            carbohydrates = carbohydrates,
            protein = protein,
            fat = fat,
            brand = brand,
            isAdminApproved = isAdminApproved,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

fun foodResultFixture(block: FoodResultFixture.() -> Unit = {}): FoodResult {
    return FoodResultFixture().apply(block).create()
}

fun foodSearchResultFixture(block: FoodSearchResultFixture.() -> Unit = {}): FoodSearchResult {
    return FoodSearchResultFixture().apply(block).create()
}
