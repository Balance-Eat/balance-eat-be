package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.LocalDateTime

class DietCommand {
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

    data class DeleteDietFood(
        val dietId: Long,
        val dietFoodId: Long
    )

    data class UpdateDietFood(
        val dietId: Long,
        val dietFoodId: Long,
        val intake: Int
    )
}