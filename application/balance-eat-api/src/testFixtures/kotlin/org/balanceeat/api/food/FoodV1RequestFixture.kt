package org.balanceeat.api.food

import org.balanceeat.common.TestFixture

class FoodV1RequestFixture {
    data class Update(
        val name: String = "테스트 음식",
        val perCapitaIntake: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0,
        val brand: String = "테스트 브랜드"
    ): TestFixture<FoodV1Request.Update> {
        override fun create(): FoodV1Request.Update {
            return FoodV1Request.Update(
                name = name,
                perCapitaIntake = perCapitaIntake,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                brand = brand
            )
        }
    }

    data class Search(
        val foodName: String? = null,
        val userId: Long? = null
    ): TestFixture<FoodV1Request.Search> {
        override fun create(): FoodV1Request.Search {
            return FoodV1Request.Search(
                foodName = foodName,
                userId = userId
            )
        }
    }
}