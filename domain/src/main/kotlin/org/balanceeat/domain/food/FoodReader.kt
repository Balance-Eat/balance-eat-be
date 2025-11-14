package org.balanceeat.domain.food

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FoodReader(
    private val foodRepository: FoodRepository
): BaseReader<Food, FoodResult>(foodRepository, FoodResult) {
    fun search(query: FoodQuery.Search): Page<FoodSearchResult> {
        return foodRepository.search(query)
    }

    override fun findByIdOrThrow(id: Long): FoodResult {
        return findByIdOrThrow(id, DomainStatus.FOOD_NOT_FOUND)
    }
}