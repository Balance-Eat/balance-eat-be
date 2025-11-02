package org.balanceeat.api.stats

import org.balanceeat.domain.diet.StatsType
import org.balanceeat.domain.stats.DietStatsAggregateResult
import java.time.LocalDate

class StatsV1Response {
    data class DietStats(
        val type: StatsType,
        val date: LocalDate,
        val totalCalories: Double,
        val totalCarbohydrates: Double,
        val totalProtein: Double,
        val totalFat: Double
    ) {
        companion object {
            fun from(type: StatsType, date: LocalDate, result: DietStatsAggregateResult): DietStats {
                return DietStats(
                    type = type,
                    date = date,
                    totalCalories = result.totalCalories,
                    totalCarbohydrates = result.totalCarbohydrates,
                    totalProtein = result.totalProtein,
                    totalFat = result.totalFat
                )
            }

            fun empty(type: StatsType, date: LocalDate): DietStats {
                return DietStats(
                    type = type,
                    date = date,
                    totalCalories = 0.0,
                    totalCarbohydrates = 0.0,
                    totalProtein = 0.0,
                    totalFat = 0.0
                )
            }
        }
    }
}
