package org.balanceeat.api.food

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Size
import org.balanceeat.domain.food.FoodDto
import java.time.LocalDateTime

class FoodV1Request {
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        val uuid: String,
        
        @field:NotNull(message = "name은 필수입니다")
        val name: String,
        
        @field:NotNull(message = "perCapitaIntake는 필수입니다")
        val perCapitaIntake: Double,

        @field:NotNull(message = "unit은 필수입니다")
        val unit: String,

        @field:NotNull(message = "carbohydrates는 필수입니다")
        val carbohydrates: Double,

        @field:NotNull(message = "protein은 필수입니다")
        val protein: Double,

        @field:NotNull(message = "fat는 필수입니다")
        val fat: Double,

        @field:NotNull(message = "brand는 필수입니다")
        val brand: String
    )

    data class Update(
        @field:NotNull(message = "음식명은 필수입니다")
        val name: String,

        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        val perCapitaIntake: Double,

        @field:NotNull(message = "단위는 필수입니다")
        val unit: String,

        @field:NotNull(message = "탄수화물 함량은 필수입니다")
        val carbohydrates: Double,

        @field:NotNull(message = "단백질 함량은 필수입니다")
        val protein: Double,

        @field:NotNull(message = "지방 함량은 필수입니다")
        val fat: Double,

        @field:NotNull(message = "브랜드는 필수입니다")
        val brand: String
    )

    data class Search(
        val foodName: String?,
        val userId: Long?,
    )
}

class FoodV1Response {
    data class Info(
        val id: Long,
        val uuid: String,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val brand: String? = null,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun from(food: FoodDto) = Info(
                id = food.id,
                uuid = food.uuid,
                name = food.name,
                perCapitaIntake = food.perCapitaIntake,
                unit = food.unit,
                carbohydrates = food.carbohydrates,
                protein = food.protein,
                fat = food.fat,
                brand = food.brand,
                createdAt = food.createdAt
            )
        }
    }
}