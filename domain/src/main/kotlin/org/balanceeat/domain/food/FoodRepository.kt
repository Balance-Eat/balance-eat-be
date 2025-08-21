package org.balanceeat.domain.food

import org.springframework.data.jpa.repository.JpaRepository

interface FoodRepository : JpaRepository<Food, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Food>
}