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
        var isAdminApproved: Boolean = false
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
                isAdminApproved = isAdminApproved
            )
        }
    }
    
    class Update(
        var foodId: Long = 1L,
        var modifierId: Long = 1L,
        var name: String = "테스트 음식",
        var perCapitaIntake: Double = 1.0,
        var unit: String = "g",
        var carbohydrates: Double = 20.0,
        var protein: Double = 5.0,
        var fat: Double = 2.0,
    ) : TestFixture<FoodCommand.Update> {
        override fun create(): FoodCommand.Update {
            return FoodCommand.Update(
                id = foodId,
                modifierId = modifierId,
                name = name,
                perCapitaIntake = perCapitaIntake,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat
            )
        }
    }

    class UpdateByAdmin(
        var foodId: Long = 1L,
        var adminId: Long = 1L,
        var name: String = "관리자 수정 음식",
        var perCapitaIntake: Double = 1.0,
        var unit: String = "g",
        var carbohydrates: Double = 0.0,
        var protein: Double = 0.0,
        var fat: Double = 0.0,
        var isAdminApproved: Boolean = true
    ) : TestFixture<FoodCommand.UpdateByAdmin> {
        override fun create(): FoodCommand.UpdateByAdmin {
            return FoodCommand.UpdateByAdmin(
                id = foodId,
                adminId = adminId,
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