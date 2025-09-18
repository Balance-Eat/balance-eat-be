package org.balanceeat.domain.food

import java.time.LocalDateTime

data class FoodDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val userId: Long,
    val perCapitaIntake: Double,
    val unit: String,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val brand: String,
    val isAdminApproved: Boolean,
    val createdAt: LocalDateTime
) {
    fun calculateNutrition(actualServingSize: Double): NutritionInfo {
        val ratio = actualServingSize / perCapitaIntake
        
        return NutritionInfo(
            calories = calculateCalories(ratio),
            carbohydrates = carbohydrates * ratio,
            protein = protein * ratio,
            fat = fat * ratio
        )
    }
    
    private fun calculateCalories(ratio: Double): Double {
        // 칼로리 계산: 탄수화물(4kcal/g) + 단백질(4kcal/g) + 지방(9kcal/g)
        return (carbohydrates * 4 + protein * 4 + fat * 9) * ratio
    }
    
    data class NutritionInfo(
        val calories: Double,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double
    )
    
    companion object {
        fun from(food: Food): FoodDto {
            return FoodDto(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                userId = food.userId,
                perCapitaIntake = food.perCapitaIntake,
                unit = food.unit,
                carbohydrates = food.carbohydrates,
                protein = food.protein,
                fat = food.fat,
                brand = food.brand,
                isAdminApproved = food.isAdminApproved,
                createdAt = food.createdAt
            )
        }
    }
}