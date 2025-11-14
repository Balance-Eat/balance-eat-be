package org.balanceeat.api.diet

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.diet.*
import org.balanceeat.domain.diet.Diet.MealType
import org.balanceeat.domain.food.FoodResult
import org.balanceeat.domain.food.NutritionInfo
import java.time.LocalDate
import java.time.LocalDateTime

class DietV1Request {
    data class Create(
        @field:NotNull(message = "식사 유형은 필수입니다")
        val mealType: MealType,
        
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

    data class Update(
        @field:NotNull(message = "식사 유형은 필수입니다")
        val mealType: MealType,

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

    data class UpdateDietFood(
        @field:NotNull(message = "섭취량은 필수입니다")
        val intake: Int
    )
}

class DietV1Response {
    data class Summary(
        val dietId: Long,
        val consumeDate: LocalDate,
        val consumedAt: LocalDateTime,
        val mealType: MealType,
        val items: List<DietFoodResponse>
    ) {
        companion object {
            fun from(summary: DietSummary): Summary {
                return Summary(
                    dietId = summary.id,
                    consumeDate = summary.consumedAt.toLocalDate(),
                    consumedAt = summary.consumedAt,
                    mealType = summary.mealType,
                    items = summary.dietFoods.map { dietFood -> DietFoodResponse.from(dietFood) }
                )
            }
        }
    }

    data class DietFoodResponse(
        val foodId: Long,
        val foodName: String,
        val intake: Int,
        val servingSize: Double,
        val unit: String,
        val calories: Double,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double
    ) {
        companion object {
            fun from(summary: DietFoodSummary): DietFoodResponse {
                return DietFoodResponse(
                    foodId = summary.foodId,
                    foodName = summary.foodName,
                    intake = summary.intake,
                    servingSize = summary.servingSize,
                    unit = summary.unit,
                    calories = summary.nutrition.calories,
                    carbohydrates = summary.nutrition.carbohydrates,
                    protein = summary.nutrition.protein,
                    fat = summary.nutrition.fat
                )
            }
        }
    }
    
    data class Details(
        val dietId: Long,
        val userId: Long,
        val mealType: MealType,
        val consumeDate: LocalDate,
        val consumedAt: LocalDateTime,
        val totalNutrition: NutritionInfo,
        val dietFoods: List<DietFood>
    ) {
        companion object {
            fun from(dietDetails: DietDetails): Details {
                return Details(
                    dietId = dietDetails.id,
                    userId = dietDetails.userId,
                    mealType = dietDetails.mealType,
                    consumeDate = dietDetails.consumedAt.toLocalDate(),
                    consumedAt = dietDetails.consumedAt,
                    totalNutrition = dietDetails.totalNutrition,
                    dietFoods = dietDetails.dietFoods.map { DietFood.from(it) }
                )
            }
        }

        data class DietFood(
            val id: Long,
            val foodId: Long,
            val foodName: String,
            val intake: Int,
            val servingSize: Double,
            val nutrition: NutritionInfo
        ) {
            companion object {
                fun from(dietFood: DietFoodDetails): DietFood {
                    return DietFood(
                        id = dietFood.dietFoodId,
                        foodId = dietFood.foodId,
                        foodName = dietFood.foodName,
                        intake = dietFood.intake,
                        servingSize = dietFood.servingSize,
                        nutrition = dietFood.nutrition
                    )
                }
            }
        }
    }
}