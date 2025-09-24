package org.balanceeat.api.food

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.common.utils.CalorieCalculator
import java.util.UUID

class FoodV1RequestFixture {
    data class Create(
        val uuid: String = UUID.randomUUID().toString(),
        val name: String = "테스트 음식",
        val servingSize: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0,
        val perServingCalories: Double = CalorieCalculator.calculate(carbohydrates, protein, fat),
        val brand: String = "테스트 브랜드"
    ): TestFixture<FoodV1Request.Create> {
        override fun create(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = uuid,
                name = name,
                servingSize = servingSize,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                perServingCalories = perServingCalories,
                brand = brand
            )
        }
    }

    data class Update(
        val name: String = "테스트 음식",
        val servingSize: Double = 100.0,
        val unit: String = "g",
        val carbohydrates: Double = 25.0,
        val protein: Double = 8.0,
        val fat: Double = 3.0,
        val perServingCalories: Double = 159.0, // 25*4 + 8*4 + 3*9 = 159
        val brand: String = "테스트 브랜드"
    ): TestFixture<FoodV1Request.Update> {
        override fun create(): FoodV1Request.Update {
            return FoodV1Request.Update(
                name = name,
                servingSize = servingSize,
                unit = unit,
                carbohydrates = carbohydrates,
                protein = protein,
                fat = fat,
                perServingCalories = perServingCalories,
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