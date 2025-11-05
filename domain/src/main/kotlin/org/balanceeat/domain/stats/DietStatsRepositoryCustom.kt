package org.balanceeat.domain.stats

import org.balanceeat.domain.diet.StatsType
import java.time.LocalDate

interface DietStatsRepositoryCustom {
    fun aggregate(statsDate: LocalDate, userId: Long): DietStats
    fun aggregate(statsDate: LocalDate, userIds: List<Long>): List<DietStats>
    fun createAll(dietStatsList: List<DietStats>)
    fun getStats(userId: Long, type: StatsType, from: LocalDate, to: LocalDate): List<DietStatsAggregateResult>
}