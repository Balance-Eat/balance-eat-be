package org.balanceeat.domain.stats

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class DietStatsDto(
    val id: Long,
    val userId: Long,
    val statsDate: String,
    val totalCalories: Double,
    val totalCarbohydrates: Double,
    val totalProtein: Double,
    val totalFat: Double
) {
    companion object {
        fun from(dietStats: DietStats): DietStatsDto {
            return DietStatsDto(
                id = dietStats.id,
                userId = dietStats.userId,
                statsDate = dietStats.statsDate.toString(),
                totalCalories = dietStats.totalCalories,
                totalCarbohydrates = dietStats.totalCarbohydrates,
                totalProtein = dietStats.totalProtein,
                totalFat = dietStats.totalFat
            )
        }
    }
}

@QueryProjection
data class DietStatsAggregateResult(
    val userId: Long,
    val statsDate: LocalDate,
    val totalCalories: Double,
    val totalCarbohydrates: Double,
    val totalProtein: Double,
    val totalFat: Double
)