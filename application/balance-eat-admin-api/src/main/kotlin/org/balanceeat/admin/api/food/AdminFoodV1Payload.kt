package org.balanceeat.admin.api.food

import jakarta.validation.constraints.NotNull
import org.balanceeat.domain.food.FoodDto

class AdminFoodV1Request {
    data class Update(
        @field:NotNull(message = "name은 필수입니다")
        val name: String,

        @field:NotNull(message = "perCapitaIntake은 필수입니다")
        val perCapitaIntake: Double,

        @field:NotNull(message = "unit은 필수입니다")
        val unit: String,

        @field:NotNull(message = "carbohydrates은 필수입니다")
        val carbohydrates: Double,

        @field:NotNull(message = "protein은 필수입니다")
        val protein: Double,

        @field:NotNull(message = "fat은 필수입니다")
        val fat: Double,

        @field:NotNull(message = "isAdminApproved는 필수입니다")
        val isAdminApproved: Boolean,

        @field:NotNull(message = "brand는 필수입니다")
        val brand: String
    )
}

class AdminFoodV1Response {
    data class Info(
        val id: Long,
        val uuid: String,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val brand: String,
        val isAdminApproved: Boolean
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
                isAdminApproved = food.isAdminApproved
            )
        }
    }
}