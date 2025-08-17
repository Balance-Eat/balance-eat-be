package org.balanceeat.api.diet

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.balanceeat.domain.diet.Diet
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class DietV1Request {
    
    data class Create(
        @field:NotNull(message = "사용자 ID는 필수입니다")
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        val userId: Long,
        
        @field:NotNull(message = "식사 유형은 필수입니다")
        @Schema(description = "식사 유형", requiredMode = Schema.RequiredMode.REQUIRED)
        val mealType: Diet.MealType,
        
        @field:NotNull(message = "음식명은 필수입니다")
        @Schema(description = "음식명", requiredMode = Schema.RequiredMode.REQUIRED)
        val foodName: String,
        
        @Schema(description = "음식 브랜드", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val foodBrand: String? = null,
        
        @field:NotNull(message = "섭취량은 필수입니다")
        @field:Positive(message = "섭취량은 양수여야 합니다")
        @Schema(description = "섭취량", requiredMode = Schema.RequiredMode.REQUIRED)
        val servingSize: BigDecimal,
        
        @field:NotNull(message = "섭취량 단위는 필수입니다")
        @Schema(description = "섭취량 단위", requiredMode = Schema.RequiredMode.REQUIRED)
        val servingUnit: String,
        
        @field:NotNull(message = "칼로리는 필수입니다")
        @field:Positive(message = "칼로리는 양수여야 합니다")
        @Schema(description = "칼로리", requiredMode = Schema.RequiredMode.REQUIRED)
        val calories: BigDecimal,
        
        @Schema(description = "탄수화물 (g)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val carbohydrates: BigDecimal? = null,
        
        @Schema(description = "단백질 (g)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val protein: BigDecimal? = null,
        
        @Schema(description = "지방 (g)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val fat: BigDecimal? = null,
        
        @Schema(description = "당류 (g)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val sugar: BigDecimal? = null,
        
        @Schema(description = "나트륨 (mg)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val sodium: BigDecimal? = null,
        
        @Schema(description = "식이섬유 (g)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val fiber: BigDecimal? = null,
        
        @field:NotNull(message = "식사 날짜는 필수입니다")
        @Schema(description = "식사 날짜", requiredMode = Schema.RequiredMode.REQUIRED)
        val mealDate: LocalDate,
        
        @Schema(description = "섭취 시간", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        val consumedAt: LocalDateTime? = null
    )
}

class DietV1Response {
    
    data class Info(
        val id: Long,
        val userId: Long,
        val mealType: Diet.MealType,
        val mealTypeName: String,
        val foodName: String,
        val foodBrand: String?,
        val servingSize: BigDecimal,
        val servingUnit: String,
        val calories: BigDecimal,
        val carbohydrates: BigDecimal?,
        val protein: BigDecimal?,
        val fat: BigDecimal?,
        val sugar: BigDecimal?,
        val sodium: BigDecimal?,
        val fiber: BigDecimal?,
        val mealDate: LocalDate,
        val consumedAt: LocalDateTime?,
        val createdAt: LocalDateTime
    )
    
    data class Summary(
        val mealDate: LocalDate,
        val totalCalories: BigDecimal,
        val totalCarbohydrates: BigDecimal?,
        val totalProtein: BigDecimal?,
        val totalFat: BigDecimal?,
        val mealCounts: Map<Diet.MealType, Int>,
        val diets: List<Info>
    )
}