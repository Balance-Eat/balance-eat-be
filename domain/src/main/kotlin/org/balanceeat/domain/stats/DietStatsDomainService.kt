package org.balanceeat.domain.stats

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus.DIET_NOT_FOUND
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.diet.DietRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

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
        val existingStats = dietStatsRepository.findByUserIdAndStatsDate(diet.userId, statsDate)
        val newDietStats = dietStatsRepository.aggregate(statsDate, diet.userId)

        if (existingStats != null) {
            existingStats.update(newDietStats)
            dietStatsRepository.save(existingStats)
        } else {
            dietStatsRepository.save(newDietStats)
        }
    }
}