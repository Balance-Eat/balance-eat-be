package org.balanceeat.domain.stats

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.diet.StatsType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Transactional(readOnly = true)
class DietStatsReader(
    private val dietStatsRepository: DietStatsRepository
): BaseReader<DietStats, DietStatsResult>(dietStatsRepository, DietStatsResult) {

    override fun findByIdOrThrow(id: Long): DietStatsResult {
        return findByIdOrThrow(id, DomainStatus.DIET_STATS_NOT_FOUND)
    }

    fun findByUserIdAndStatsDate(userId: Long, statsDate: LocalDate): DietStatsResult? {
        return dietStatsRepository.findByUserIdAndStatsDate(userId, statsDate)
            ?.let { DietStatsResult.from(it) }
    }

    fun getStats(userId: Long, type: StatsType, from: LocalDate, to: LocalDate): List<DietStatsAggregateResult> {
        return dietStatsRepository.getStats(userId, type, from, to)
    }

}
