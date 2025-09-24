package org.balanceeat.domain.food

import java.time.LocalDateTime

data class FoodDto(
    val id: Long,
    val uuid: String,
    val name: String,
    val userId: Long,
    val servingSize: Double,
    val unit: String,
    val perServingCalories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val brand: String,
    val isAdminApproved: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(food: Food): FoodDto {
            return FoodDto(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                userId = food.userId,
                servingSize = food.servingSize,
                unit = food.unit,
                perServingCalories = food.perServingCalories,
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