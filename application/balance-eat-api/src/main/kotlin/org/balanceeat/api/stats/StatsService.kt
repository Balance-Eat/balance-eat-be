package org.balanceeat.api.stats

import org.balanceeat.domain.stats.DietStatsRepository
import org.balanceeat.domain.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StatsService(
    private val dietStatsRepository: DietStatsRepository,
    private val userRepository: UserRepository
) {
    companion object {
        private const val BATCH_SIZE = 1000
    }

    fun aggregateStats(statsDate: LocalDate) {
        dietStatsRepository.deleteByStatsDate(statsDate)

        val totalUserCount = userRepository.count()

        for (offset in 0 until totalUserCount step BATCH_SIZE.toLong()) {
            val pageRequest = PageRequest.of(offset.toInt() / BATCH_SIZE, BATCH_SIZE)
            val userIds = userRepository.findAll(pageRequest)
                .map { it.id }
                .toList()

            val aggregatedStats = dietStatsRepository.aggregate(statsDate, userIds)
            dietStatsRepository.createAll(aggregatedStats)
        }
    }
}