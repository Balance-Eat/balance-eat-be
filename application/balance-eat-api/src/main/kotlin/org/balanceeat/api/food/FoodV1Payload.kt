package org.balanceeat.api.food

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Size
import org.balanceeat.domain.food.FoodDto

class FoodV1Request {
    @Schema(name = "FoodCreateRequest", description = "음식 생성 요청")
    data class Create(
        @field:NotNull(message = "UUID는 필수입니다")
        @Schema(
            description = "음식 UUID",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val uuid: String,
        
        @field:NotNull(message = "음식명은 필수입니다")
        @field:Size(min = 1, max = 100, message = "음식명은 1자 이상 100자 이하여야 합니다")
        @Schema(
            description = "음식명",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val name: String,
        
        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        @field:Positive(message = "1회 기준 섭취량은 0보다 큰 값이어야 합니다")
        @field:DecimalMax(value = "10000.0", message = "1회 기준 섭취량은 10000을 초과할 수 없습니다")
        @Schema(
            description = "1회 기준 섭취량",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val perCapitaIntake: Double,
        
        @field:NotNull(message = "단위는 필수입니다")
        @field:Size(min = 1, max = 20, message = "단위는 1자 이상 20자 이하여야 합니다")
        @Schema(
            description = "단위 (예: g, ml 등)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val unit: String,
        
        @field:DecimalMin(value = "0.0", message = "탄수화물 함량은 0 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "탄수화물 함량은 1000g을 초과할 수 없습니다")
        @Schema(
            description = "탄수화물 함량 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val carbohydrates: Double = 0.0,
        
        @field:DecimalMin(value = "0.0", message = "단백질 함량은 0 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "단백질 함량은 1000g을 초과할 수 없습니다")
        @Schema(
            description = "단백질 함량 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val protein: Double = 0.0,
        
        @field:DecimalMin(value = "0.0", message = "지방 함량은 0 이상이어야 합니다")
        @field:DecimalMax(value = "1000.0", message = "지방 함량은 1000g을 초과할 수 없습니다")
        @Schema(
            description = "지방 함량 (g, 선택)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        )
        val fat: Double = 0.0
    )

    @Schema(name = "FoodUpdateRequest", description = "음식 수정 요청")
    data class Update(
        @field:NotNull(message = "음식명은 필수입니다")
        @Schema(
            description = "음식명",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val name: String,

        @field:NotNull(message = "1회 기준 섭취량은 필수입니다")
        @Schema(
            description = "1회 기준 섭취량",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val perCapitaIntake: Double,

        @field:NotNull(message = "단위는 필수입니다")
        @Schema(
            description = "단위 (예: g, ml 등)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val unit: String,

        @field:NotNull(message = "탄수화물 함량은 필수입니다")
        @Schema(
            description = "탄수화물 함량 (g, 선택)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val carbohydrates: Double = 0.0,

        @field:NotNull(message = "단백질 함량은 필수입니다")
        @Schema(
            description = "단백질 함량 (g, 선택)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val protein: Double = 0.0,

        @field:NotNull(message = "지방 함량은 필수입니다")
        @Schema(
            description = "지방 함량 (g, 선택)",
            requiredMode = Schema.RequiredMode.REQUIRED,
        )
        val fat: Double = 0.0
    )
}

class FoodV1Response {
    @Schema(name = "FoodDetailsResponse", description = "음식 상세정보 응답")
    data class Info(
        val id: Long,
        val uuid: String,
        val name: String,
        val perCapitaIntake: Double,
        val unit: String,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double
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
                fat = food.fat
            )
        }
    }
}