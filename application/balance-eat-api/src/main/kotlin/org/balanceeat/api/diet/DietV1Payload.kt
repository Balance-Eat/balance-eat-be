package org.balanceeat.api.diet

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.NotEmpty
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.Diet.MealType
import org.balanceeat.domain.diet.DietDto
import org.balanceeat.domain.diet.DietFood
import org.balanceeat.domain.diet.DietFoodDto
import org.balanceeat.domain.diet.NutritionInfo
import org.balanceeat.domain.food.Food
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
            fun of(diet: Diet, foodMap: Map<Long, Food>): Summary {
                return Summary(
                    dietId = diet.id,
                    consumeDate = diet.consumedAt.toLocalDate(),
                    consumedAt = diet.consumedAt,
                    mealType = diet.mealType,
                    items = diet.dietFoods.map { dietFood ->
                        val food = foodMap[dietFood.foodId]
                            ?: throw DomainException(DomainStatus.FOOD_NOT_FOUND)
                        DietFoodResponse.of(dietFood, food)
                    }
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
            fun of(dietFood: DietFood, food: Food): DietFoodResponse {
                val info = food.calculateNutrition(
                    dietFood.intake.toDouble()
                )

                return DietFoodResponse(
                    foodId = dietFood.foodId,
                    foodName = food.name,
                    intake = dietFood.intake,
                    servingSize = food.servingSize,
                    unit = food.unit,
                    calories = info.calories,
                    carbohydrates = info.carbohydrates,
                    protein = info.protein,
                    fat = info.fat
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
            fun from(dietDto: DietDto): Details {
                return Details(
                    dietId = dietDto.id,
                    userId = dietDto.userId,
                    mealType = dietDto.mealType,
                    consumeDate = dietDto.consumedAt.toLocalDate(),
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
            val servingSize: Double,
            val nutrition: NutritionInfo
        ) {
            companion object {
                fun from(dietFood: DietFoodDto): DietFood {
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