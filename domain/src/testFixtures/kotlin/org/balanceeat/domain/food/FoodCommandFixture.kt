package org.balanceeat.domain.food

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.config.UuidGenerator

class FoodCommandFixture {
    class Create(
        var uuid: String = UuidGenerator.generateUuidV7().toString(),
        var name: String = "테스트 음식",
        var userId: Long = 1L,
        var servingSize: Double = 1.0,
        var unit: String = "g",
        var carbohydrates: Double = 20.0,
        var protein: Double = 5.0,
        var fat: Double = 2.0,
        var brand: String = "테스트 브랜드",
        var isAdminApproved: Boolean = false
    ) : TestFixture<FoodCommand.Create> {
        override fun create(): FoodCommand.Create {
            return FoodCommand.Create(
                uuid = uuid,
                name = name,
                userId = userId,
                servingSize = servingSize,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                brand = brand,
                isAdminApproved = isAdminApproved
            )
        }
    }
    
    class Update(
        var foodId: Long = 1L,
        var name: String = "테스트 음식",
        var servingSize: Double = 1.0,
        var unit: String = "g",
        var carbohydrates: Double = 20.0,
        var protein: Double = 5.0,
        var fat: Double = 2.0,
        var brand: String = "테스트 브랜드",
        var isAdminApproved: Boolean = false
    ) : TestFixture<FoodCommand.Update> {
        override fun create(): FoodCommand.Update {
            return FoodCommand.Update(
                id = foodId,
                name = name,
                servingSize = servingSize,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                brand = brand,
                isAdminApproved = isAdminApproved
            )
        }
    }
}

fun foodCreateCommandFixture(block: FoodCommandFixture.Create.() -> Unit = {}): FoodCommand.Create {
    return FoodCommandFixture.Create().apply(block).create()
}

fun foodUpdateCommandFixture(block: FoodCommandFixture.Update.() -> Unit = {}): FoodCommand.Update {
    return FoodCommandFixture.Update().apply(block).create()
}