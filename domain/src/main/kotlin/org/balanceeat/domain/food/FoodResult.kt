package org.balanceeat.domain.food

import com.querydsl.core.annotations.QueryProjection
import org.balanceeat.domain.common.EntityMapper
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
    companion object: EntityMapper<Food, FoodResult> {
        override fun from(entity: Food): FoodResult {
            return FoodResult(
                id = entity.id,
                uuid = entity.uuid,
                name = entity.name,
                userId = entity.userId,
                servingSize = entity.servingSize,
                unit = entity.unit,
                perServingCalories = entity.perServingCalories,
                carbohydrates = entity.carbohydrates,
                protein = entity.protein,
                fat = entity.fat,
                brand = entity.brand,
                isAdminApproved = entity.isAdminApproved,
                createdAt = entity.createdAt
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
