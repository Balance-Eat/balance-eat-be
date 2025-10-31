package org.balanceeat.api.stats

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

    enum class StatsType {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
