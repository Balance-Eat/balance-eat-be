package org.balanceeat.api.stats

import org.balanceeat.domain.stats.DietStatsWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class StatsScheduleService(
    private val dietStatsWriter: DietStatsWriter,
) {

    @Transactional
    fun aggregateStats(statsDate: LocalDate) {
        dietStatsWriter.aggregateStats(statsDate)
    }
}