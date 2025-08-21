package org.balanceeat.domain.diet

import org.balanceeat.domain.config.TestFixture
import org.balanceeat.domain.config.NEW_ID
import java.time.LocalDate
import java.time.LocalDateTime

class DietFixture(
    var id: Long = NEW_ID,
    var userId: Long = 1L,
    var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
    var mealDate: LocalDate = LocalDate.now(),
    var consumedAt: LocalDateTime = LocalDateTime.now()
) : TestFixture<Diet> {
    override fun create(): Diet {
        return Diet(
            id = id,
            userId = userId,
            mealType = mealType,
            mealDate = mealDate,
            consumedAt = consumedAt
        )
    }
}