package org.balanceeat.domain.diet

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.user.UserFixture
import org.balanceeat.domain.user.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class DietDomainServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var dietDomainService: DietDomainService

    @Autowired
    private lateinit var dietRepository: DietRepository

    @Autowired
    private lateinit var foodRepository: FoodRepository

    @Nested
    @DisplayName("식단 생성 테스트")
    inner class CreateTest {
        @Test
        fun `식단 생성 성공`() {
            // given
            val food1 = foodRepository.save(FoodFixture(
                name = "바나나",
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 27.0,
                protein = 1.3,
                fat = 0.2
            ).create())
            
            val food2 = foodRepository.save(FoodFixture(
                name = "우유",
                perCapitaIntake = 200.0,
                unit = "ml",
                carbohydrates = 9.1,
                protein = 6.6,
                fat = 3.2
            ).create())
            
            val command = DietCommand.Create(
                userId = 1L,
                mealType = Diet.MealType.BREAKFAST,
                consumedAt = LocalDateTime.now(),
                foods = listOf(
                    DietCommand.Create.Food(
                        foodId = food1.id,
                        intake = 2 // 바나나 2개
                    ),
                    DietCommand.Create.Food(
                        foodId = food2.id,
                        intake = 1 // 우유 1개
                    )
                )
            )

            // when
            val createdDiet = dietDomainService.create(command)

            // then
            assertThat(createdDiet.id).isNotNull()
            assertThat(createdDiet.userId).isEqualTo(1L)
            assertThat(createdDiet.mealType).isEqualTo(Diet.MealType.BREAKFAST)
            assertThat(createdDiet.dietFoods).hasSize(2)
            
            // 영양성분 계산 검증
            assertThat(createdDiet.totalNutrition.calories).isGreaterThan(0.0)
            assertThat(createdDiet.totalNutrition.carbohydrates).isGreaterThan(0.0)
            
            // 첫 번째 음식 검증 (바나나 2개)
            val dietFood1 = createdDiet.dietFoods.find { it.foodId == food1.id }!!
            assertThat(dietFood1.intake).isEqualTo(2)
            assertThat(dietFood1.foodName).isEqualTo("바나나")
            
            // 두 번째 음식 검증 (우유 1개)
            val dietFood2 = createdDiet.dietFoods.find { it.foodId == food2.id }!!
            assertThat(dietFood2.intake).isEqualTo(1)
            assertThat(dietFood2.foodName).isEqualTo("우유")

            // DB에 실제로 저장되었는지 검증
            assertThat(dietRepository.existsById(createdDiet.id)).isTrue()
        }
    }
}