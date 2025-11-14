package org.balanceeat.domain.stats

import com.querydsl.core.annotations.QueryProjection
import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDate

data class DietStatsResult(
    val id: Long,
    val userId: Long,
    val statsDate: LocalDate,
    val totalCalories: Double,
    val totalCarbohydrates: Double,
    val totalProtein: Double,
    val totalFat: Double
) {
    companion object: EntityMapper<DietStats, DietStatsResult> {
        override fun from(entity: DietStats): DietStatsResult {
            return DietStatsResult(
                id = entity.id,
                userId = entity.userId,
                statsDate = entity.statsDate,
                totalCalories = entity.totalCalories,
                totalCarbohydrates = entity.totalCarbohydrates,
                totalProtein = entity.totalProtein,
                totalFat = entity.totalFat
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