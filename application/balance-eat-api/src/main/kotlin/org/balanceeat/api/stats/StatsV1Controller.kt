package org.balanceeat.api.stats

import org.balanceeat.api.common.USER_ID_HEADER
import org.balanceeat.apibase.response.ApiResponse
import org.balanceeat.domain.diet.StatsType
import org.springframework.web.bind.annotation.*
import java.time.DayOfWeek
import java.time.LocalDate

@RestController
@RequestMapping("/v1/stats")
class StatsV1Controller(
    private val statsService: StatsService
) {

    @GetMapping
    fun getStats(
        @RequestHeader(USER_ID_HEADER) userId: Long,
        @RequestParam type: StatsType,
        @RequestParam from: LocalDate?,
        @RequestParam to: LocalDate?,
        @RequestParam limit: Int = 5
    ): ApiResponse<List<StatsV1Response.DietStats>> {
        return ApiResponse.success(
            statsService.getStats(
                userId = userId,
                type = type,
                from = from?: when (type) {
                    StatsType.DAILY -> LocalDate.now().minusDays(limit.toLong() - 1)
                    StatsType.WEEKLY -> LocalDate.now().minusWeeks(limit.toLong() - 1).with(DayOfWeek.MONDAY)
                    StatsType.MONTHLY -> LocalDate.now().minusMonths(limit.toLong() - 1).withDayOfMonth(1)
                },
                to = to?: LocalDate.now()
            )
        )
    }
}
