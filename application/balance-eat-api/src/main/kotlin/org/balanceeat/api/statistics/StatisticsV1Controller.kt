package org.balanceeat.api.statistics

import org.balanceeat.api.statistics.StatisticsV1Response.StatisticsType
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/stats")
class StatisticsV1Controller {

    @GetMapping
    fun getStatistics(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam type: StatisticsType
    ): ApiResponse<List<StatisticsV1Response.Statistics>> {
        val mockData = when (type) {
            StatisticsType.DAILY -> generateDailyMockData()
            StatisticsType.WEEKLY -> generateWeeklyMockData()
            StatisticsType.MONTHLY -> generateMonthlyMockData()
        }

        return ApiResponse.success(mockData)
    }

    private fun generateDailyMockData(): List<StatisticsV1Response.Statistics> {
        return listOf(
            StatisticsV1Response.Statistics(
                type = StatisticsType.DAILY,
                date = LocalDate.of(2025, 9, 23),
                totalCalories = 2000.5,
                totalCarbohydrates = 250.5,
                totalProtein = 150.5,
                totalFat = 70.5,
                weight = 70.5
            ),
            StatisticsV1Response.Statistics(
                type = StatisticsType.DAILY,
                date = LocalDate.of(2025, 9, 24),
                totalCalories = 1800.0,
                totalCarbohydrates = 220.0,
                totalProtein = 130.0,
                totalFat = 60.0,
                weight = 70.0
            )
        )
    }

    private fun generateWeeklyMockData(): List<StatisticsV1Response.Statistics> {
        return listOf(
            StatisticsV1Response.Statistics(
                type = StatisticsType.WEEKLY,
                date = LocalDate.of(2025, 9, 7),
                totalCalories = 2000.5,
                totalCarbohydrates = 250.5,
                totalProtein = 150.5,
                totalFat = 70.5,
                weight = 70.5
            ),
            StatisticsV1Response.Statistics(
                type = StatisticsType.WEEKLY,
                date = LocalDate.of(2025, 9, 14),
                totalCalories = 1800.0,
                totalCarbohydrates = 220.0,
                totalProtein = 130.0,
                totalFat = 60.0,
                weight = 70.0
            )
        )
    }

    private fun generateMonthlyMockData(): List<StatisticsV1Response.Statistics> {
        return listOf(
            StatisticsV1Response.Statistics(
                type = StatisticsType.MONTHLY,
                date = LocalDate.of(2025, 8, 1),
                totalCalories = 2000.5,
                totalCarbohydrates = 250.5,
                totalProtein = 150.5,
                totalFat = 70.5,
                weight = 70.5
            ),
            StatisticsV1Response.Statistics(
                type = StatisticsType.MONTHLY,
                date = LocalDate.of(2025, 9, 1),
                totalCalories = 1800.0,
                totalCarbohydrates = 220.0,
                totalProtein = 130.0,
                totalFat = 60.0,
                weight = 70.0
            )
        )
    }
}
