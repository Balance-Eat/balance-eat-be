package org.balanceeat.domain.diet

import org.balanceeat.common.TestFixture
import java.time.LocalDate
import java.time.LocalDateTime

class DietCommandFixture {
    class Create(
        var userId: Long = 1L,
        var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
        var consumedAt: LocalDateTime = LocalDateTime.now(),
        var dietFoods: List<DietCommand.Create.DietFood> = mutableListOf(
            DietCommand.Create.DietFood(
                foodId = 1L,
                intake = 2
            )
        )
    ) : TestFixture<DietCommand.Create> {
        override fun create(): DietCommand.Create {
            return DietCommand.Create(
                userId = userId,
                mealType = mealType,
                consumedAt = consumedAt,
                dietFoods = dietFoods
            )
        }
    }

    class Update(
        var id: Long = 1L,
        var mealType: Diet.MealType = Diet.MealType.DINNER,
        var consumedAt: LocalDateTime = LocalDateTime.now(),
        var dietFoods: List<DietCommand.Update.DietFood> = mutableListOf(
            DietCommand.Update.DietFood(
                foodId = 1L,
                intake = 3
            )
        )
    ) : TestFixture<DietCommand.Update> {
        override fun create(): DietCommand.Update {
            return DietCommand.Update(
                id = id,
                mealType = mealType,
                consumedAt = consumedAt,
                dietFoods = dietFoods
            )
        }
    }

    class DeleteDietFood(
        var dietId: Long = 1L,
        var dietFoodId: Long = 1L
    ) : TestFixture<DietCommand.DeleteDietFood> {
        override fun create(): DietCommand.DeleteDietFood {
            return DietCommand.DeleteDietFood(
                dietId = dietId,
                dietFoodId = dietFoodId
            )
        }
    }

    class UpdateDietFood(
        var dietId: Long = 1L,
        var dietFoodId: Long = 1L,
        var intake: Int = 150
    ) : TestFixture<DietCommand.UpdateDietFood> {
        override fun create(): DietCommand.UpdateDietFood {
            return DietCommand.UpdateDietFood(
                dietId = dietId,
                dietFoodId = dietFoodId,
                intake = intake
            )
        }
    }
}

fun dietCreateCommandFixture(block: DietCommandFixture.Create.() -> Unit = {}): DietCommand.Create {
    return DietCommandFixture.Create().apply(block).create()
}

fun dietUpdateCommandFixture(block: DietCommandFixture.Update.() -> Unit = {}): DietCommand.Update {
    return DietCommandFixture.Update().apply(block).create()
}

fun dietDeleteDietFoodCommandFixture(block: DietCommandFixture.DeleteDietFood.() -> Unit = {}): DietCommand.DeleteDietFood {
    return DietCommandFixture.DeleteDietFood().apply(block).create()
}

fun dietUpdateDietFoodCommandFixture(block: DietCommandFixture.UpdateDietFood.() -> Unit = {}): DietCommand.UpdateDietFood {
    return DietCommandFixture.UpdateDietFood().apply(block).create()
}