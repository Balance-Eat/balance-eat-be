package org.balanceeat.api.diet

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class DietV1Response {
    data class DailyDietInfo(
        @Schema(description = "해당 날짜의 총 영양 정보")
        val dailyTotal: DailyTotal,
        @Schema(description = "식사 기록 목록")
        val diets: List<Diet>
    )

    data class DailyTotal(
        @Schema(description = "총 칼로리 (kcal)")
        val totalCalorie: Int,
        @Schema(description = "총 탄수화물 (g)")
        val totalCarbohydrates: Int,
        @Schema(description = "총 단백질 (g)")
        val totalProtein: Int,
        @Schema(description = "총 지방 (g)")
        val totalFat: Int
    )

    data class Diet(
        @Schema(description = "식사 기록 고유 ID (DIET 테이블의 id)")
        val dietId: Long,
        @Schema(description = "식사 시간 (ISO 8601)")
        val eatingAt: String,
        @Schema(description = "식사 유형 (아침, 점심, 저녁, 간식 등)")
        val type: String,
        @Schema(description = "해당 식사에 포함된 음식 목록")
        val items: List<DietItem>
    )

    data class DietItem(
        @Schema(description = "음식 고유 ID (FOOD 테이블의 id)")
        val foodId: Long,
        @Schema(description = "음식 이름 (FOOD 테이블의 name)")
        val foodName: String,
        @Schema(description = "섭취량 (DIET_ITEM 테이블의 intake)")
        val intake: Int,
        @Schema(description = "섭취량 단위 (FOOD 테이블의 unit)")
        val unit: String,
        @Schema(description = "해당 섭취량의 칼로리 (계산된 값)")
        val calories: Int,
        @Schema(description = "해당 섭취량의 탄수화물 (계산된 값)")
        val carbohydrates: Int,
        @Schema(description = "해당 섭취량의 단백질 (계산된 값)")
        val protein: Int,
        @Schema(description = "해당 섭취량의 지방 (계산된 값)")
        val fat: Int
    )
}