package org.balanceeat.domain.diet

import java.time.LocalDate
import java.time.LocalDateTime

sealed class DietCommand {
    
    data class Create(
        val userId: Long,
        val mealType: Diet.MealType,
        val mealDate: LocalDate,
        val consumedAt: LocalDateTime? = null,
        val foods: List<AddFood> = emptyList()
    )
    
    data class AddFood(
        val foodId: Long,
        val actualServingSize: Double,
        val servingUnit: String
    )
    
    data class RemoveFood(
        val dietId: Long,
        val dietFoodId: Long
    )
    
    data class UpdateFood(
        val dietFoodId: Long,
        val actualServingSize: Double,
        val servingUnit: String
    )
    
    data class Search(
        val userId: Long? = null,
        val userUuid: String? = null,
        val mealDate: LocalDate,
        val mealType: Diet.MealType? = null
    )
}