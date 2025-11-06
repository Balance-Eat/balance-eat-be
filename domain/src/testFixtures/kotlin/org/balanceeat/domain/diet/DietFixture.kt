package org.balanceeat.domain.diet

import org.balanceeat.domain.config.NEW_ID
import org.balanceeat.domain.config.TestFixture
import java.time.LocalDate
import java.time.LocalDateTime

class DietFixture(
    var id: Long = NEW_ID,
    var userId: Long = 1L,
    var mealType: Diet.MealType = Diet.MealType.BREAKFAST,
    var consumedAt: LocalDateTime = LocalDate.now().atStartOfDay(),
    var dietFoods: MutableList<DietFood> = mutableListOf(
        DietFoodFixture().create()
    )
) : TestFixture<Diet> {
    override fun create(): Diet {
        return Diet(
            id = id,
            userId = userId,
            mealType = mealType,
            consumedAt = consumedAt,
            dietFoods = dietFoods
        )
    }
}

fun dietFixture(block: DietFixture.() -> Unit = {}): Diet {
    return DietFixture().apply(block).create()
}