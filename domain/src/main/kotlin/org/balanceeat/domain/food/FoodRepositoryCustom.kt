package org.balanceeat.domain.food

import org.springframework.data.domain.Page


interface FoodRepositoryCustom {
    fun search(query: FoodQuery.Search): Page<FoodSearchResult>
}