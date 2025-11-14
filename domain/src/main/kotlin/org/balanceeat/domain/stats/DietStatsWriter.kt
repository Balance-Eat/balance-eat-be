package org.balanceeat.domain.stats

import org.balanceeat.domain.common.DomainStatus.DIET_NOT_FOUND
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class DietStatsWriter(
    private val dietRepository: DietRepository,
    private val dietStatsRepository: DietStatsRepository,
    private val userRepository: UserRepository
) {
    companion object {
        private const val BATCH_SIZE = 1000
    }
    @Transactional
    fun upsert(dietId: Long) {
        val diet = dietRepository.findByIdOrNull(dietId)
            ?: throw EntityNotFoundException(DIET_NOT_FOUND)
        val statsDate = diet.consumedAt.toLocalDate()
        upsert(diet.userId, statsDate)
    }

    @Transactional
    fun upsert(userId: Long, statsDate: LocalDate) {
        val existingStats = dietStatsRepository.findByUserIdAndStatsDate(userId, statsDate)
        val newDietStats = dietStatsRepository.aggregate(statsDate, userId)

        if (existingStats != null) {
            existingStats.update(newDietStats)
            dietStatsRepository.save(existingStats)
        } else {
            dietStatsRepository.save(newDietStats)
        }
    }

    @Transactional
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
