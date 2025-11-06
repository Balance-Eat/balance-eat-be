package org.balanceeat.domain.stats

import org.balanceeat.domain.config.TestFixture
import java.time.LocalDate

class DietStatsFixture(
    var userId: Long = 1L,
    var statsDate: LocalDate = LocalDate.now(),
    var totalCalories: Double = 2000.0,
    var totalCarbohydrates: Double = 250.0,
    var totalProtein: Double = 100.0,
    var totalFat: Double = 60.0
) : TestFixture<DietStats> {
    override fun create(): DietStats {
        return DietStats(
            userId = userId,
            statsDate = statsDate,
            totalCalories = totalCalories,
            totalCarbohydrates = totalCarbohydrates,
            totalProtein = totalProtein,
            totalFat = totalFat
        )
    }
}

fun dietStatsFixture(block: DietStatsFixture.() -> Unit = {}): DietStats {
    return DietStatsFixture().apply(block).create()
}
