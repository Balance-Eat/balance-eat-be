package org.balanceeat.domain.curation

import java.time.LocalDateTime

data class CurationFoodResult(
    val id: Long,
    val foodId: Long,
    val weight: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(curationFood: CurationFood): CurationFoodResult {
            return CurationFoodResult(
                id = curationFood.id,
                foodId = curationFood.foodId,
                weight = curationFood.weight,
                createdAt = curationFood.createdAt
            )
        }
    }
}