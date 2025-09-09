package org.balanceeat.api.diet

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.user.UserFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class DietServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var dietService: DietService

    @Nested
    @DisplayName("식단 생성 테스트")
    inner class CreateTest {
        @Test
        fun `식단을 성공적으로 생성할 수 있다`() {
            // given
            val user = createEntity(UserFixture().create())
            val food1 = createEntity(FoodFixture(
                name = "토스트",
                perCapitaIntake = 30.0,
                unit = "g",
                carbohydrates = 15.0,
                protein = 2.5,
                fat = 1.5
            ).create())
            
            val food2 = createEntity(FoodFixture(
                name = "우유",
                perCapitaIntake = 200.0,
                unit = "ml",
                carbohydrates = 9.0,
                protein = 6.6,
                fat = 3.2
            ).create())
            
            val food3 = createEntity(FoodFixture(
                name = "바나나",
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 27.0,
                protein = 1.3,
                fat = 0.2
            ).create())
            
            val request = DietV1Request.Create(
                mealType = "BREAKFAST",
                consumedAt = LocalDateTime.of(2025, 1, 15, 8, 0),
                dietFoods = listOf(
                    DietV1Request.Create.DietFood(
                        foodId = food1.id,
                        intake = 2
                    ),
                    DietV1Request.Create.DietFood(
                        foodId = food2.id,
                        intake = 1
                    ),
                    DietV1Request.Create.DietFood(
                        foodId = food3.id,
                        intake = 1
                    )
                )
            )

            // when
            val result = dietService.create(request, user.id)

            // then
            assertThat(result.mealType).isEqualTo(Diet.MealType.BREAKFAST)
            assertThat(result.consumedAt).isEqualTo(LocalDateTime.of(2025, 1, 15, 8, 0))
            assertThat(result.dietFoods).hasSize(3)
            
            // 각 음식의 섭취량 검증
            val toastFood = result.dietFoods.find { it.foodId == food1.id }!!
            assertThat(toastFood.intake).isEqualTo(2)
            assertThat(toastFood.foodName).isEqualTo("토스트")
            
            val milkFood = result.dietFoods.find { it.foodId == food2.id }!!
            assertThat(milkFood.intake).isEqualTo(1)
            assertThat(milkFood.foodName).isEqualTo("우유")
            
            val bananaFood = result.dietFoods.find { it.foodId == food3.id }!!
            assertThat(bananaFood.intake).isEqualTo(1)
            assertThat(bananaFood.foodName).isEqualTo("바나나")
            
            // 총 영양성분이 합산되었는지 검증
            val expectedCalories = toastFood.nutrition.calories + milkFood.nutrition.calories + bananaFood.nutrition.calories
            assertThat(result.totalNutrition.calories).isEqualTo(expectedCalories)
        }
    }
}