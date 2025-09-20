package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.LocalDateTime

sealed class DietCommand {
    data class Create(
        val userId: Long,
        val mealType: Diet.MealType,
        val consumedAt: LocalDateTime,
        val foods: List<Food> = emptyList()
    ) {
        data class Food(
            val foodId: Long,
            val intake: Int
        )
    }

    data class FindDailyDiets(
        val userId: Long,
        val date: LocalDate
    )
}