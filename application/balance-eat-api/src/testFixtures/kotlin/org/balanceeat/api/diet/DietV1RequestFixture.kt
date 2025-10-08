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

    data class Update(
        val mealType: Diet.MealType = Diet.MealType.DINNER,
        val consumedAt: LocalDateTime = LocalDateTime.now(),
        val dietFoods: List<DietV1Request.Update.DietFood> = listOf(
            DietV1Request.Update.DietFood(foodId = 1L, intake = 3),
            DietV1Request.Update.DietFood(foodId = 3L, intake = 2)
        )
    ) : TestFixture<DietV1Request.Update> {
        override fun create(): DietV1Request.Update {
            return DietV1Request.Update(
                mealType = mealType,
                consumedAt = consumedAt,
                dietFoods = dietFoods
            )
        }
    }
}