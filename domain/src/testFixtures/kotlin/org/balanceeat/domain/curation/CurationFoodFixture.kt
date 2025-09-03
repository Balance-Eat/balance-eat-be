package org.balanceeat.domain.curation

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.NEW_ID

class CurationFoodFixture(
    var id: Long = NEW_ID,
    var foodId: Long = 1L,
    var weight: Int = 100
) : TestFixture<CurationFood> {
    override fun create(): CurationFood {
        return CurationFood(
            id = id,
            foodId = foodId,
            weight = weight
        )
    }
}