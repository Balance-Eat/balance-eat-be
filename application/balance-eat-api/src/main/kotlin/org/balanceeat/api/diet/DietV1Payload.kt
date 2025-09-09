package org.balanceeat.api.diet

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.NotEmpty
import org.balanceeat.domain.diet.Diet.MealType
import org.balanceeat.domain.diet.DietDto
import org.balanceeat.domain.diet.DietFoodDto
import org.balanceeat.domain.diet.NutritionInfo
import java.time.LocalDateTime

class DietV1Request {
    data class Create(
        @field:NotNull(message = "식사 유형은 필수입니다")
        val mealType: String,
        
        @field:NotNull(message = "섭취 시간은 필수입니다")
        val consumedAt: LocalDateTime,
        
        @field:NotEmpty(message = "음식 목록은 최소 1개 이상이어야 합니다")
        @field:Valid
        val dietFoods: List<DietFood>
    ) {
        data class DietFood(
            @field:NotNull(message = "음식 ID는 필수입니다")
            val foodId: Long,

            @field:NotNull(message = "섭취량은 필수입니다")
            val intake: Int
        )
    }
}

class DietV1Response {

    data class DailyDietInfo(
        val dailyTotal: DailyTotal,
        val diets: List<Diet>
    )

    data class DailyTotal(
        val totalCalorie: Int,
        val totalCarbohydrates: Int,
        val totalProtein: Int,
        val totalFat: Int
    )

    data class Diet(
        val dietId: Long,
        val eatingAt: String,
        val type: String,
        val items: List<DietItem>
    )

    data class DietItem(
        val foodId: Long,
        val foodName: String,
        val intake: Int,
        val unit: String,
        val calories: Int,
        val carbohydrates: Int,
        val protein: Int,
        val fat: Int
    )
    
    data class Details(
        val dietId: Long,
        val userId: Long,
        val mealType: MealType,
        val consumedAt: LocalDateTime,
        val totalNutrition: NutritionInfo,
        val dietFoods: List<DietFood>
    ) {
        companion object {
            fun from(dietDto: DietDto): Details {
                return Details(
                    dietId = dietDto.id,
                    userId = dietDto.userId,
                    mealType = dietDto.mealType,
                    consumedAt = dietDto.consumedAt,
                    totalNutrition = dietDto.totalNutrition,
                    dietFoods = dietDto.dietFoods.map { DietFood.from(it) }
                )
            }
        }

        data class DietFood(
            val id: Long,
            val foodId: Long,
            val foodName: String,
            val intake: Int,
            val nutrition: NutritionInfo
        ) {
            companion object {
                fun from(dietFood: DietFoodDto): DietFood {
                    return DietFood(
                        id = dietFood.dietFoodId,
                        foodId = dietFood.foodId,
                        foodName = dietFood.foodName,
                        intake = dietFood.intake,
                        nutrition = dietFood.nutrition
                    )
                }
            }
        }
    }
}