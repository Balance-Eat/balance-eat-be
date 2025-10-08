package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.LocalDateTime

sealed class DietCommand {
    data class Create(
        val userId: Long,
        val mealType: Diet.MealType,
        val consumedAt: LocalDateTime,
        val dietFoods: List<DietFood> = emptyList()
    ) {
        data class DietFood(
            val foodId: Long,
            val intake: Int
        )
    }

    data class Update(
        val id: Long,
        val mealType: Diet.MealType,
        val consumedAt: LocalDateTime,
        val dietFoods: List<DietFood>
    ) {
        data class DietFood(
            val foodId: Long,
            val intake: Int
        )
    }

    data class FindDailyDiets(
        val userId: Long,
        val date: LocalDate
    )
}