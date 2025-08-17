package org.balanceeat.domain.diet

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

object DietFixture {
    
    fun createDiet(
        userId: Long,
        mealType: Diet.MealType = Diet.MealType.BREAKFAST,
        mealDate: LocalDate = LocalDate.now()
    ): Diet {
        return Diet(
            userId = userId,
            mealType = mealType,
            mealDate = mealDate,
            consumedAt = LocalDateTime.now()
        )
    }
    
    fun createDietCommand(
        userId: Long,
        mealType: Diet.MealType = Diet.MealType.BREAKFAST,
        mealDate: LocalDate = LocalDate.now(),
        foodIds: List<Long> = emptyList()
    ): DietCommand.Create {
        val foods = foodIds.map { foodId ->
            DietCommand.AddFood(
                foodId = foodId,
                actualServingSize = BigDecimal("1.0"),
                servingUnit = "개"
            )
        }
        
        return DietCommand.Create(
            userId = userId,
            mealType = mealType,
            mealDate = mealDate,
            consumedAt = LocalDateTime.now(),
            foods = foods
        )
    }
    
    fun createDietFood(
        dietId: Long,
        foodId: Long,
        actualServingSize: BigDecimal = BigDecimal("1.0"),
        servingUnit: String = "개"
    ): DietFood {
        return DietFood(
            dietId = dietId,
            foodId = foodId,
            actualServingSize = actualServingSize,
            servingUnit = servingUnit,
            calculatedCalories = BigDecimal("100.0"),
            calculatedCarbohydrates = BigDecimal("20.0"),
            calculatedProtein = BigDecimal("5.0"),
            calculatedFat = BigDecimal("2.0"),
            calculatedSugar = BigDecimal("1.0"),
            calculatedSodium = BigDecimal("100.0"),
            calculatedFiber = BigDecimal("1.0")
        )
    }
}