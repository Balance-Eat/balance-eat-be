package org.balanceeat.domain.stats

import org.balanceeat.domain.config.TestFixture
import java.time.LocalDate

class DietStatsResultFixture(
    var id: Long = 1L,
    var userId: Long = 1L,
    var statsDate: String = LocalDate.now().toString(),
    var totalCalories: Double = 2000.0,
    var totalCarbohydrates: Double = 250.0,
    var totalProtein: Double = 100.0,
    var totalFat: Double = 70.0
) : TestFixture<DietStatsResult> {
    override fun create(): DietStatsResult {
        return DietStatsResult(
            id = id,
            userId = userId,
            statsDate = statsDate,
            totalCalories = totalCalories,
            totalCarbohydrates = totalCarbohydrates,
            totalProtein = totalProtein,
            totalFat = totalFat
        )
    }
}

fun dietStatsResultFixture(block: DietStatsResultFixture.() -> Unit = {}): DietStatsResult {
    return DietStatsResultFixture().apply(block).create()
}

class DietStatsAggregateResultFixture(
    var userId: Long = 1L,
    var statsDate: LocalDate = LocalDate.now(),
    var totalCalories: Double = 2000.0,
    var totalCarbohydrates: Double = 250.0,
    var totalProtein: Double = 100.0,
    var totalFat: Double = 70.0
) : TestFixture<DietStatsAggregateResult> {
    override fun create(): DietStatsAggregateResult {
        return DietStatsAggregateResult(
            userId = userId,
            statsDate = statsDate,
            totalCalories = totalCalories,
            totalCarbohydrates = totalCarbohydrates,
            totalProtein = totalProtein,
            totalFat = totalFat
        )
    }
}

fun dietStatsAggregateResultFixture(block: DietStatsAggregateResultFixture.() -> Unit = {}): DietStatsAggregateResult {
    return DietStatsAggregateResultFixture().apply(block).create()
}
