package org.balanceeat.api.stats

import org.balanceeat.domain.diet.StatsType
import java.time.LocalDate

class StatsV1Response {
    data class DietStats(
        val type: StatsType,
        val date: LocalDate,
        val totalCalories: Double,
        val totalCarbohydrates: Double,
        val totalProtein: Double,
        val totalFat: Double
    )
}
