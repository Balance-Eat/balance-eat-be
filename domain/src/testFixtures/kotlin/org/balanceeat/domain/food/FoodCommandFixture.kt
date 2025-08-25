package org.balanceeat.domain.food

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.UuidGenerator

class FoodCommandFixture(
    var uuid: String = UuidGenerator.generateUuidV7().toString(),
    var name: String = "테스트 음식",
    var perCapitaIntake: Double = 1.0,
    var unit: String = "g",
    var carbohydrates: Double = 20.0,
    var protein: Double = 5.0,
    var fat: Double = 2.0
) : TestFixture<FoodCommand.Create> {
    override fun create(): FoodCommand.Create {
        return FoodCommand.Create(
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