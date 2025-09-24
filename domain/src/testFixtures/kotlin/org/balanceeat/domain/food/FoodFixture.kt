package org.balanceeat.domain.food

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.NEW_ID
import org.balanceeat.domain.config.UuidGenerator

class FoodFixture(
    var id: Long = NEW_ID,
    var uuid: String = UuidGenerator.generateUuidV7().toString(),
    var name: String = "테스트 음식",
    var userId: Long = 1L,
    var servingSize: Double = 1.0,
    var unit: String = "g",
    var carbohydrates: Double = 20.0,
    var protein: Double = 5.0,
    var fat: Double = 2.0,
    var perServingCalories: Double = 118.0, // 20*4 + 5*4 + 2*9 = 118
    var brand: String = "테스트 브랜드",
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
            perServingCalories = perServingCalories,
            brand = brand,
            isAdminApproved = isAdminApproved
        )
    }
}