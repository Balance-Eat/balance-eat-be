package org.balanceeat.api.diet

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.diet.Diet
import java.time.LocalDateTime

class DietV1RequestFixture {
    data class Create(
        val mealType: Diet.MealType = Diet.MealType.BREAKFAST,
        val consumedAt: LocalDateTime = LocalDateTime.now(),
        val dietFoods: List<DietV1Request.Create.DietFood> = listOf(
            DietV1Request.Create.DietFood(foodId = 1L, intake = 2),
            DietV1Request.Create.DietFood(foodId = 2L, intake = 1)
        )
    ) : TestFixture<DietV1Request.Create> {
        override fun create(): DietV1Request.Create {
            return DietV1Request.Create(
                mealType = mealType,
                consumedAt = consumedAt,
                dietFoods = dietFoods
            )
        }
    }
}