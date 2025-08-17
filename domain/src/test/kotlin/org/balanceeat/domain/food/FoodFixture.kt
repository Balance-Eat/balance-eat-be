package org.balanceeat.domain.food

import java.math.BigDecimal

object FoodFixture {
    
    fun createFood(
        name: String = "테스트 음식",
        brand: String? = "테스트 브랜드",
        caloriesPerServing: BigDecimal = BigDecimal("100.0")
    ): Food {
        return Food(
            name = name,
            brand = brand,
            servingSize = BigDecimal("1.0"),
            servingUnit = "개",
            caloriesPerServing = caloriesPerServing,
            carbohydratesPerServing = BigDecimal("20.0"),
            proteinPerServing = BigDecimal("5.0"),
            fatPerServing = BigDecimal("2.0"),
            sugarPerServing = BigDecimal("1.0"),
            sodiumPerServing = BigDecimal("100.0"),
            fiberPerServing = BigDecimal("1.0"),
            description = "테스트용 음식입니다",
            category = "테스트",
            isVerified = true
        )
    }
    
    fun createFoodCommand(
        name: String = "테스트 음식",
        brand: String? = "테스트 브랜드",
        caloriesPerServing: BigDecimal = BigDecimal("100.0")
    ): FoodCommand.Create {
        return FoodCommand.Create(
            name = name,
            brand = brand,
            servingSize = BigDecimal("1.0"),
            servingUnit = "개",
            caloriesPerServing = caloriesPerServing,
            carbohydratesPerServing = BigDecimal("20.0"),
            proteinPerServing = BigDecimal("5.0"),
            fatPerServing = BigDecimal("2.0"),
            sugarPerServing = BigDecimal("1.0"),
            sodiumPerServing = BigDecimal("100.0"),
            fiberPerServing = BigDecimal("1.0"),
            description = "테스트용 음식입니다",
            category = "테스트",
            isVerified = true
        )
    }
}