package org.balanceeat.domain.diet

import org.balanceeat.domain.food.Food
import java.time.LocalDateTime

data class DietDto(
    val id: Long,
    val userId: Long,
    val mealType: Diet.MealType,
    val consumedAt: LocalDateTime,
    val totalNutrition: NutritionInfo,
    val dietFoods: List<DietFoodDto>
) {
    companion object {
        fun from(diet: Diet, foodMap: Map<Long, Food>): DietDto {
            val dietFoods = diet.dietFoods.map { dietFood ->
                val food = foodMap[dietFood.foodId]!!
                DietFoodDto.from(dietFood, food)
            }
            
            val totalNutrition = NutritionInfo(
                calories = dietFoods.sumOf { it.nutrition.calories },
                carbohydrates = dietFoods.sumOf { it.nutrition.carbohydrates },
                protein = dietFoods.sumOf { it.nutrition.protein },
                fat = dietFoods.sumOf { it.nutrition.fat }
            )
            
            return DietDto(
                id = diet.id,
                userId = diet.userId,
                mealType = diet.mealType,
                consumedAt = diet.consumedAt,
                totalNutrition = totalNutrition,
                dietFoods = dietFoods
            )
        }
    }
}

data class DietFoodDto(
    val dietFoodId: Long,
    val foodId: Long,
    val foodName: String,
    val intake: Int,
    val servingSize: Double,
    val nutrition: NutritionInfo
) {
    companion object {
        fun from(dietFood: DietFood, food: Food): DietFoodDto {
            val nutrition = food.calculateNutrition(dietFood.intake.toDouble())
            return DietFoodDto(
                dietFoodId = dietFood.id,
                foodId = dietFood.foodId,
                foodName = food.name,
                intake = dietFood.intake,
                servingSize = food.servingSize,
                nutrition = NutritionInfo(
                    calories = nutrition.calories,
                    carbohydrates = nutrition.carbohydrates,
                    protein = nutrition.protein,
                    fat = nutrition.fat
                )
            )
        }
    }
}

data class NutritionInfo(
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
)

enum class StatsType {
    DAILY,
    WEEKLY,
    MONTHLY
}