package org.balanceeat.api.stats

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class StatsScheduler(
    private val statsScheduleService: StatsScheduleService
) {
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun aggregateDailyStats() {
        val statsDate = LocalDate.now().minusDays(1)
        statsScheduleService.aggregateStats(statsDate)
    }
}
