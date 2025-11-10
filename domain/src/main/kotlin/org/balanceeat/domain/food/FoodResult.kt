package org.balanceeat.domain.food

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class FoodResult(
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
        fun from(food: Food): FoodResult {
            return FoodResult(
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

@QueryProjection
data class FoodSearchResult(
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
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
