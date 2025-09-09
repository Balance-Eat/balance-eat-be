package org.balanceeat.api.diet

import org.balanceeat.common.TestFixture
import java.time.LocalDateTime

class DietV1RequestFixture {
    data class CreateDiet(
        val mealType: String = "BREAKFAST",
        val consumedAt: LocalDateTime = LocalDateTime.now(),
        val dietFoods: List<DietV1Request.Create.Companion.DietFood> = listOf(
            DietV1Request.Create.Companion.DietFood(foodId = 1L, intake = 2),
            DietV1Request.Create.Companion.DietFood(foodId = 2L, intake = 1)
        )
    ) : TestFixture<DietV1Request.Create> {
        override fun create(): DietV1Request.Create {
            return DietV1Request.Create(
                mealType = mealType,
                consumedAt = consumedAt,
                dietFoods = dietFoods
            )
        }
        
        fun withMealType(mealType: String) = copy(mealType = mealType)
        fun withConsumedAt(consumedAt: LocalDateTime) = copy(consumedAt = consumedAt)
        fun withDietFoods(dietFoods: List<DietV1Request.Create.Companion.DietFood>) = copy(dietFoods = dietFoods)
    }
    
    data class DietFoodRequest(
        val foodId: Long = 1L,
        val intake: Int = 1
    ) : TestFixture<DietV1Request.Create.Companion.DietFood> {
        override fun create(): DietV1Request.Create.Companion.DietFood {
            return DietV1Request.Create.Companion.DietFood(
                foodId = foodId,
                intake = intake
            )
        }
    }
}