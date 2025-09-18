package org.balanceeat.domain.food

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.UuidGenerator
import java.time.LocalDateTime

class FoodSearchResultFixture(
    var id: Long = 1L,
    var uuid: String = UuidGenerator.generateUuidV7().toString(),
    var name: String = "테스트 음식",
    var userId: Long = 1L,
    var perCapitaIntake: Double = 100.0,
    var unit: String = "g",
    var carbohydrates: Double = 25.0,
    var protein: Double = 8.0,
    var fat: Double = 3.0,
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
            perCapitaIntake = perCapitaIntake,
            unit = unit,
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