package org.balanceeat.api.statistics

import java.time.LocalDate

class StatisticsV1Response {
    data class Statistics(
        val type: StatisticsType,
        val date: LocalDate,
        val totalCalories: Double,
        val totalCarbohydrates: Double,
        val totalProtein: Double,
        val totalFat: Double,
        val weight: Double
    )

    enum class StatisticsType {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
