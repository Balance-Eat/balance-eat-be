package org.balanceeat.api.diet

import java.time.LocalDateTime

class DietV1Response {
    data class DailyDietInfo(
        val dailyTotal: DailyTotal,
        val diets: List<Diet>
    )

    data class DailyTotal(
        val totalCalorie: Int,
        val totalCarbohydrates: Int,
        val totalProtein: Int,
        val totalFat: Int
    )

    data class Diet(
        val dietId: Long,
        val eatingAt: String,
        val type: String,
        val items: List<DietItem>
    )

    data class DietItem(
        val foodId: Long,
        val foodName: String,
        val intake: Int,
        val unit: String,
        val calories: Int,
        val carbohydrates: Int,
        val protein: Int,
        val fat: Int
    )
}