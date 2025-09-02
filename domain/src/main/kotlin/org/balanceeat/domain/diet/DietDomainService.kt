package org.balanceeat.domain.diet

import org.balanceeat.domain.common.DomainService
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@DomainService
class DietDomainService(
    private val dietRepository: DietRepository,
    private val dietFoodRepository: DietFoodRepository
) {
    @Transactional(readOnly = true)
    fun getDiet(dietId: Long): Diet {
        return dietRepository.findById(dietId)
            .orElseThrow { EntityNotFoundException(DomainStatus.DIET_NOT_FOUND) }
    }
    
    @Transactional(readOnly = true)
    fun getDietFood(dietFoodId: Long): DietFood {
        return dietFoodRepository.findById(dietFoodId)
            .orElseThrow { EntityNotFoundException(DomainStatus.DIET_FOOD_NOT_FOUND) }
    }
    
    @Transactional(readOnly = true)
    fun findByUserIdAndDate(userId: Long, date: LocalDate): List<Diet> {
        return dietRepository.findByUserIdAndMealDateOrderByMealTypeAndCreatedAt(userId, date)
    }
    
    data class NutritionSummary(
        val calories: Double,
        val carbohydrates: Double,
        val protein: Double,
        val fat: Double,
        val sugar: Double,
        val sodium: Double,
        val fiber: Double
    )
}