package org.balanceeat.domain.diet

import java.time.LocalDateTime

class DietCommandFixture(
    var userId: Long = 1L,
    var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
    var consumedAt: LocalDateTime = LocalDateTime.now(),
    var foods: List<DietCommand.Create.Food> = mutableListOf(
        DietCommand.Create.Food(
            foodId = 1L,
            intake = 2
        )
    )
) {
    fun create(): DietCommand.Create {
        return DietCommand.Create(
            userId = userId,
            mealType = mealType,
            consumedAt = consumedAt,
            foods = foods
        )
    }
}