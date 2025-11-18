package org.balanceeat.domain.diet

import com.querydsl.core.annotations.QueryProjection
import org.balanceeat.domain.common.EntityMapper
import org.balanceeat.domain.food.Food
import org.balanceeat.domain.food.NutritionInfo
import java.time.LocalDateTime

data class DietResult(
    val id: Long,
    val userId: Long,
    val mealType: Diet.MealType,
    val consumedAt: LocalDateTime,
    val dietFoods: List<DietFoodResult>
) {
    companion object: EntityMapper<Diet, DietResult> {
        override fun from(entity: Diet): DietResult {
            val dietFoods = entity.dietFoods.map { dietFood ->
                DietFoodResult(
                    id = dietFood.id,
                    dietFoodId = dietFood.id,
                    foodId = dietFood.foodId,
                    intake = dietFood.intake
                )
            }

            return DietResult(
                id = entity.id,
                userId = entity.userId,
                mealType = entity.mealType,
                consumedAt = entity.consumedAt,
                dietFoods = dietFoods
            )
        }
    }
}

data class DietFoodResult(
    val id: Long,
    val dietFoodId: Long,
    val foodId: Long,
    val intake: Int,
)

data class DietDetails(
    val id: Long,
    val userId: Long,
    val mealType: Diet.MealType,
    val consumedAt: LocalDateTime,
    val totalNutrition: NutritionInfo,
    val dietFoods: List<DietFoodDetails>
) {
    companion object {
        fun from(diet: Diet, foodMap: Map<Long, Food>): DietDetails {
            val dietFoods = diet.dietFoods.map { dietFood ->
                val food = foodMap[dietFood.foodId]!!
                DietFoodDetails.from(dietFood, food)
            }

            val totalNutrition = NutritionInfo(
                calories = dietFoods.sumOf { it.nutrition.calories },
                carbohydrates = dietFoods.sumOf { it.nutrition.carbohydrates },
                protein = dietFoods.sumOf { it.nutrition.protein },
                fat = dietFoods.sumOf { it.nutrition.fat }
            )

            return DietDetails(
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

data class DietFoodDetails(
    val dietFoodId: Long,
    val foodId: Long,
    val foodName: String,
    val intake: Int,
    val servingSize: Double,
    val nutrition: NutritionInfo
) {
    companion object {
        fun from(dietFood: DietFood, food: Food): DietFoodDetails {
            return DietFoodDetails(
                dietFoodId = dietFood.id,
                foodId = dietFood.foodId,
                foodName = food.name,
                intake = dietFood.intake,
                servingSize = food.servingSize,
                nutrition = food.calculateNutrition(dietFood.intake.toDouble())
            )
        }
    }
}

data class DietSummary(
    val id: Long,
    val userId: Long,
    val mealType: Diet.MealType,
    val consumedAt: LocalDateTime,
    val dietFoods: List<DietFoodSummary>
) {
    companion object {
        fun of(diet: Diet, foodMap: Map<Long, Food>): DietSummary {
            val dietFoods = diet.dietFoods.map { dietFood ->
                val food = foodMap[dietFood.foodId]!!
                DietFoodSummary.of(dietFood, food)
            }

            return DietSummary(
                id = diet.id,
                userId = diet.userId,
                mealType = diet.mealType,
                consumedAt = diet.consumedAt,
                dietFoods = dietFoods
            )
        }
    }
}

data class DietFoodSummary(
    val dietFoodId: Long,
    val foodId: Long,
    val foodName: String,
    val intake: Int,
    val unit: String,
    val servingSize: Double,
    val nutrition: NutritionInfo
) {
    companion object {
        fun of(dietFood: DietFood, food: Food): DietFoodSummary {
            return DietFoodSummary(
                dietFoodId = dietFood.id,
                foodId = dietFood.foodId,
                foodName = food.name,
                intake = dietFood.intake,
                unit = food.unit,
                servingSize = food.servingSize,
                nutrition = food.calculateNutrition(dietFood.intake.toDouble())
            )
        }
    }
}

enum class StatsType {
    DAILY,
    WEEKLY,
    MONTHLY
}