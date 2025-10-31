package org.balanceeat.domain.stats

import java.time.LocalDate

interface DietStatsRepositoryCustom {
    fun aggregate(statsDate: LocalDate, userIds: List<Long>): List<DietStats>
    fun createAll(dietStatsList: List<DietStats>)
}