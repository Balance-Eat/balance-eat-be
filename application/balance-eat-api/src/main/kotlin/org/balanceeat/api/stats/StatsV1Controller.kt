package org.balanceeat.api.stats

import org.balanceeat.apibase.response.ApiResponse
import org.balanceeat.domain.diet.StatsType
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/stats")
class StatsV1Controller {

    @GetMapping
    fun getStats(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam type: StatsType
    ): ApiResponse<List<StatsV1Response.DietStats>> {
        val mockData = when (type) {
            StatsType.DAILY -> generateDailyMockData()
            StatsType.WEEKLY -> generateWeeklyMockData()
            StatsType.MONTHLY -> generateMonthlyMockData()
        }

        return ApiResponse.success(mockData)
    }

    private fun generateDailyMockData(): List<StatsV1Response.DietStats> {
        return listOf(
            StatsV1Response.DietStats(
                type = StatsType.DAILY,
                date = LocalDate.of(2025, 9, 23),
                totalCalories = 2000.5,
                totalCarbohydrates = 250.5,
                totalProtein = 150.5,
                totalFat = 70.5
            ),
            StatsV1Response.DietStats(
                type = StatsType.DAILY,
                date = LocalDate.of(2025, 9, 24),
                totalCalories = 1800.0,
                totalCarbohydrates = 220.0,
                totalProtein = 130.0,
                totalFat = 60.0
            )
        )
    }

    private fun generateWeeklyMockData(): List<StatsV1Response.DietStats> {
        return listOf(
            StatsV1Response.DietStats(
                type = StatsType.WEEKLY,
                date = LocalDate.of(2025, 9, 7),
                totalCalories = 2000.5,
                totalCarbohydrates = 250.5,
                totalProtein = 150.5,
                totalFat = 70.5
            ),
            StatsV1Response.DietStats(
                type = StatsType.WEEKLY,
                date = LocalDate.of(2025, 9, 14),
                totalCalories = 1800.0,
                totalCarbohydrates = 220.0,
                totalProtein = 130.0,
                totalFat = 60.0
            )
        )
    }

    private fun generateMonthlyMockData(): List<StatsV1Response.DietStats> {
        return listOf(
            StatsV1Response.DietStats(
                type = StatsType.MONTHLY,
                date = LocalDate.of(2025, 8, 1),
                totalCalories = 2000.5,
                totalCarbohydrates = 250.5,
                totalProtein = 150.5,
                totalFat = 70.5
            ),
            StatsV1Response.DietStats(
                type = StatsType.MONTHLY,
                date = LocalDate.of(2025, 9, 1),
                totalCalories = 1800.0,
                totalCarbohydrates = 220.0,
                totalProtein = 130.0,
                totalFat = 60.0
            )
        )
    }
}
