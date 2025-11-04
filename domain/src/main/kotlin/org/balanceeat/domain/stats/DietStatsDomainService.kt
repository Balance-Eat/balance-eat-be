package org.balanceeat.domain.stats

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus.DIET_NOT_FOUND
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.diet.DietRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@DomainService
class DietStatsDomainService(
    private val dietRepository: DietRepository,
    private val dietStatsRepository: DietStatsRepository
){
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
            ?: DietStats.empty(userId, statsDate)

        if (existingStats != null) {
            existingStats.update(newDietStats)
            dietStatsRepository.save(existingStats)
        } else {
            dietStatsRepository.save(newDietStats)
        }
    }
}