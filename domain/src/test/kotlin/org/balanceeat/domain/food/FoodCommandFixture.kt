package org.balanceeat.domain.food

import org.balanceeat.domain.config.TestFixture

class FoodCommandFixture(
    var name: String = "테스트 음식",
    var brand: String? = "테스트 브랜드",
    var servingSize: Double = 1.0,
    var servingUnit: String = "개",
    var caloriesPerServing: Double = 100.0,
    var carbohydratesPerServing: Double = 20.0,
    var proteinPerServing: Double = 5.0,
    var fatPerServing: Double = 2.0,
    var sugarPerServing: Double = 1.0,
    var sodiumPerServing: Double = 100.0,
    var fiberPerServing: Double = 1.0,
    var description: String? = "테스트용 음식입니다",
    var category: String = "테스트",
    var isVerified: Boolean = true
) : TestFixture<FoodCommand.Create> {
    override fun create(): FoodCommand.Create {
        return FoodCommand.Create(
            name = name,
            brand = brand,
            servingSize = servingSize,
            servingUnit = servingUnit,
            caloriesPerServing = caloriesPerServing,
            carbohydratesPerServing = carbohydratesPerServing,
            proteinPerServing = proteinPerServing,
            fatPerServing = fatPerServing,
            sugarPerServing = sugarPerServing,
            sodiumPerServing = sodiumPerServing,
            fiberPerServing = fiberPerServing,
            description = description,
            category = category,
            isVerified = isVerified
        )
    }
}