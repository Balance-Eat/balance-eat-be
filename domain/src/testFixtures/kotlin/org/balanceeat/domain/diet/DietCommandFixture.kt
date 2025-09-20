package org.balanceeat.domain.diet

import org.balanceeat.common.TestFixture
import java.time.LocalDate
import java.time.LocalDateTime

class DietCommandFixture {
    class Create(
        var userId: Long = 1L,
        var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
        var consumedAt: LocalDateTime = LocalDateTime.now(),
        var foods: List<DietCommand.Create.Food> = mutableListOf(
            DietCommand.Create.Food(
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
                foods = foods
            )
        }
    }

    class FindDailyDiets(
        var userId: Long = 1L,
        var date: LocalDate = LocalDate.now()
    ) : TestFixture<DietCommand.FindDailyDiets> {
        override fun create(): DietCommand.FindDailyDiets {
            return DietCommand.FindDailyDiets(
                userId = userId,
                date = date
            )
        }
    }
}