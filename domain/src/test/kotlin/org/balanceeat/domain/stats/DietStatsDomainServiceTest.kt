package org.balanceeat.domain.stats

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietFixture
import org.balanceeat.domain.diet.DietFoodFixture
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.user.UserFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DietStatsDomainServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var dietStatsDomainService: DietStatsDomainService

    @Autowired
    private lateinit var dietRepository: DietRepository

    @Autowired
    private lateinit var dietStatsRepository: DietStatsRepository

    @Autowired
    private lateinit var foodRepository: FoodRepository

    @Nested
    @DisplayName("식단 통계 생성 테스트")
    inner class UpsertTest {
        @Test
        fun `새로운 통계 생성 성공`() {
            // given
            val user = createEntity(UserFixture().create())
            val food = createEntity(FoodFixture().create())
            val breakfast = dietRepository.save(
                DietFixture(
                    userId = user.id,
                    mealType = Diet.MealType.BREAKFAST,
                    dietFoods = mutableListOf(
                        DietFoodFixture(
                            foodId = food.id,
                            intake = food.servingSize.toInt()
                        ).create()
                    )
                ).create()
            )
            val lunch = dietRepository.save(
                DietFixture(
                    userId = user.id,
                    mealType = Diet.MealType.LUNCH,
                    consumedAt = breakfast.consumedAt.plusHours(4),
                    dietFoods = mutableListOf(
                        DietFoodFixture(
                            foodId = food.id,
                            intake = food.servingSize.toInt() * 2
                        ).create()
                    )
                ).create()
            )

            val statsDate = lunch.consumedAt.toLocalDate()

            // when
            dietStatsDomainService.upsert(lunch.id)

            // then
            val savedStats = dietStatsRepository.findByUserIdAndStatsDate(lunch.userId, statsDate)!!
            assertThat(savedStats.userId).isEqualTo(lunch.userId)
            assertThat(savedStats.statsDate).isEqualTo(statsDate)
            assertThat(savedStats.totalCalories).isEqualTo(food.perServingCalories * 3)
            assertThat(savedStats.totalCarbohydrates).isEqualTo(food.carbohydrates * 3)
            assertThat(savedStats.totalProtein).isEqualTo(food.protein * 3)
            assertThat(savedStats.totalFat).isEqualTo(food.fat * 3)
        }

        @Test
        fun `기존 통계를 업데이트 성공`() {
            // given
            val user = createEntity(UserFixture().create())
            val food = createEntity(FoodFixture().create())
            val diet = dietRepository.save(
                DietFixture(
                    userId = user.id,
                    dietFoods = mutableListOf(
                        DietFoodFixture(
                            foodId = food.id,
                            intake = food.servingSize.toInt()
                        ).create()
                    )
                ).create()
            )

            val statsDate = diet.consumedAt.toLocalDate()

            // 기존 통계 생성
            val existingStats = DietStats(
                userId = diet.userId,
                statsDate = statsDate,
                totalCalories = 100.0,
                totalCarbohydrates = 10.0,
                totalProtein = 10.0,
                totalFat = 5.0
            )
            createEntity(existingStats)

            val beforeUpdate = dietStatsRepository.findByUserIdAndStatsDate(diet.userId, statsDate)
            assertThat(beforeUpdate!!.totalCalories).isEqualTo(100.0)

            // when
            dietStatsDomainService.upsert(diet.id)

            // then
            val updatedStats = dietStatsRepository.findByUserIdAndStatsDate(diet.userId, statsDate)!!
            assertThat(updatedStats.id).isEqualTo(existingStats.id)
            assertThat(updatedStats.totalCalories).isEqualTo(food.perServingCalories)
            assertThat(updatedStats.totalCarbohydrates).isEqualTo(food.carbohydrates)
            assertThat(updatedStats.totalProtein).isEqualTo(food.protein)
            assertThat(updatedStats.totalFat).isEqualTo(food.fat)
        }
    }
}