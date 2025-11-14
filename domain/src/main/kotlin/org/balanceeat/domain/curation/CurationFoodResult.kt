package org.balanceeat.domain.curation

import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDateTime

data class CurationFoodResult(
    val id: Long,
    val foodId: Long,
    val weight: Int,
    val createdAt: LocalDateTime
) {
    companion object: EntityMapper<CurationFood, CurationFoodResult> {
        override fun from(entity: CurationFood): CurationFoodResult {
            return CurationFoodResult(
                id = entity.id,
                foodId = entity.foodId,
                weight = entity.weight,
                createdAt = entity.createdAt
            )
        }
    }
}