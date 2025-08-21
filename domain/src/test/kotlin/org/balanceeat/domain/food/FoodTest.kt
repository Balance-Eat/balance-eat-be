package org.balanceeat.domain.food

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FoodTest {
    @Test
    fun `Food 엔티티의 영양성분 계산이 정확하다`() {
        // given
        val food = Food(
            name = "테스트 음식",
            perCapitaIntake = 100.0,
            unit = "g",
            carbohydrates = 20.0,
            protein = 5.0,
            fat = 2.0
        )

        // when
        val nutrition = food.calculateNutrition(200.0) // 2배 섭취

        // then
        assertEquals(40.0, nutrition.carbohydrates) // 20 * 2
        assertEquals(10.0, nutrition.protein) // 5 * 2
        assertEquals(4.0, nutrition.fat) // 2 * 2
        assertEquals(236.0, nutrition.calories) // (20*4 + 5*4 + 2*9) * 2 = 118 * 2
    }
}