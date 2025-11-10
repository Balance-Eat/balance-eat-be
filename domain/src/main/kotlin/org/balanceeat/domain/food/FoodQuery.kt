package org.balanceeat.domain.food

import org.springframework.data.domain.Pageable

class FoodQuery {
    data class Search(
        val foodName: String?,
        val userId: Long?,
        val pageable: Pageable
    )
}