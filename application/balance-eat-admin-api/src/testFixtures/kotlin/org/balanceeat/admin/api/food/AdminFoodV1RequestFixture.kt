package org.balanceeat.admin.api.food

import org.balanceeat.common.TestFixture

class AdminFoodV1RequestFixture {
    class Update(
        var name: String = "수정된 음식",
        var perCapitaIntake: Double = 150.0,
        var unit: String = "g",
        var carbohydrates: Double = 30.0,
        var protein: Double = 10.0,
        var fat: Double = 5.0,
        var brand: String = "수정된 브랜드",
        var isAdminApproved: Boolean = true
    ) : TestFixture<AdminFoodV1Request.Update> {
        override fun create(): AdminFoodV1Request.Update {
            return AdminFoodV1Request.Update(
                name = name,
                perCapitaIntake = perCapitaIntake,
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