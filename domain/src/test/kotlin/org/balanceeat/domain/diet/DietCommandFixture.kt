package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.LocalDateTime

class DietCommandFixture(
    var userId: Long = 1L,
    var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
    var mealDate: LocalDate = LocalDate.now(),
    var consumedAt: LocalDateTime = LocalDateTime.now(),
    var foods: MutableList<DietCommand.AddFood> = mutableListOf(
        DietCommand.AddFood(
            foodId = 1L,
            actualServingSize = 1.0,
            servingUnit = "개"
        )
    )
) {
    fun create(): DietCommand.Create {
        return DietCommand.Create(
            userId = userId,
            mealType = mealType,
            mealDate = mealDate,
            consumedAt = consumedAt,
            foods = foods
        )
    }
}