package org.balanceeat.domain.diet

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.NEW_ID

class DietFoodFixture(
    var id: Long = NEW_ID,
    var foodId: Long = 1L,
    var intake: Int = 1
) : TestFixture<DietFood> {
    override fun create(): DietFood {
        return DietFood(
            id = id,
            foodId = foodId,
            intake = intake
        )
    }
}

fun dietFoodFixture(block: DietFoodFixture.() -> Unit = {}): DietFood {
    return DietFoodFixture().apply(block).create()
}