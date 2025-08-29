package org.balanceeat.domain.food

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.UuidGenerator

class FoodCommandFixture {
    class Create(
        var uuid: String = UuidGenerator.generateUuidV7().toString(),
        var name: String = "테스트 음식",
        var userId: Long = 1L,
        var perCapitaIntake: Double = 1.0,
        var unit: String = "g",
        var carbohydrates: Double = 20.0,
        var protein: Double = 5.0,
        var fat: Double = 2.0,
        var isVerified: Boolean = false
    ) : TestFixture<FoodCommand.Create> {
        override fun create(): FoodCommand.Create {
            return FoodCommand.Create(
                uuid = uuid,
                name = name,
                userId = userId,
                perCapitaIntake = perCapitaIntake,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                isVerified = isVerified
            )
        }
    }
    
    class Update(
        var foodId: Long = 1L,
        var userId: Long = 1L,
        var name: String? = null,
        var perCapitaIntake: Double? = null,
        var unit: String? = null,
        var carbohydrates: Double? = null,
        var protein: Double? = null,
        var fat: Double? = null,
        var isVerified: Boolean? = null
    ) : TestFixture<FoodCommand.Update> {
        override fun create(): FoodCommand.Update {
            return FoodCommand.Update(
                foodId = foodId,
                userId = userId,
                name = name,
                perCapitaIntake = perCapitaIntake,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                isVerified = isVerified
            )
        }
    }
}