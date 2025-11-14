package org.balanceeat.domain.curation

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class CurationFoodReader(
    private val curationFoodRepository: CurationFoodRepository
): BaseReader<CurationFood, CurationFoodResult>(curationFoodRepository, CurationFoodResult) {
    fun findRecommendedFoods(pageable: Pageable): List<CurationFood> {
        return curationFoodRepository.findRecommendedFoods(pageable)
    }

    override fun findByIdOrThrow(id: Long): CurationFoodResult {
        return findByIdOrThrow(id, DomainStatus.CURATION_FOOD_NOT_FOUND)
    }
}