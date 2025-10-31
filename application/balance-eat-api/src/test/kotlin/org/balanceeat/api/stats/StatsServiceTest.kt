package org.balanceeat.api.stats

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietFixture
import org.balanceeat.domain.diet.DietFoodFixture
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.stats.DietStats
import org.balanceeat.domain.stats.DietStatsFixture
import org.balanceeat.domain.user.UserFixture
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional
class StatsServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var statsService: StatsService
    @Autowired
    private lateinit var dietRepository: DietRepository

    @Nested
    inner class AggregateStatsTest {
        @Test
        fun `식단 통계 집계가 정상 동작한다`() {
            // given
            val statsDate = LocalDate.now()
            val user = createEntity(UserFixture().create())
            val food = createEntity(FoodFixture().create())

            val diet1 = dietRepository.save(
                DietFixture(
                    userId = user.id,
                    mealType = Diet.MealType.BREAKFAST,
                    consumedAt = statsDate.atStartOfDay(),
                    dietFoods = mutableListOf(
                        DietFoodFixture(
                            foodId = food.id,
                            intake = 100
                        ).create()
                    )
                ).create()
            )

            val diet2 = dietRepository.save(
                DietFixture(
                    userId = user.id,
                    mealType = Diet.MealType.LUNCH,
                    consumedAt = statsDate.atStartOfDay().plusHours(5),
                    dietFoods = mutableListOf(
                        DietFoodFixture(
                            foodId = food.id,
                            intake = 80
                        ).create()
                    )
                ).create()
            )
            val expectedTotalCalories = food.perServingCalories * (100 + 80) / food.servingSize
            val expectedTotalCarbohydrates = food.carbohydrates * (100 + 80) / food.servingSize
            val expectedTotalProtein = food.protein * (100 + 80) / food.servingSize
            val expectedTotalFat = food.fat * (100 + 80) / food.servingSize

            // when
            statsService.aggregateStats(statsDate)

            // then
            val stats = getAllEntities(DietStats::class.java)

            assertThat(stats)
                .hasSize(1)
                .allMatch { stat ->
                    stat.userId == user.id &&
                    stat.statsDate == statsDate &&
                    stat.totalCalories == expectedTotalCalories &&
                    stat.totalCarbohydrates == expectedTotalCarbohydrates &&
                    stat.totalProtein == expectedTotalProtein &&
                    stat.totalFat == expectedTotalFat
                }
        }

        @Test
        fun `동일 날짜의 기존 통계가 삭제 후 재집계된다`() {
            // given
            val statsDate = LocalDate.now()
            val user = createEntity(UserFixture().create())
            val food = createEntity(FoodFixture().create())

            val alreadyExistStats = createEntity(DietStatsFixture(
                userId = user.id,
                statsDate = statsDate
            ).create())

            val diet = createEntity(
                DietFixture(
                    userId = user.id,
                    mealType = Diet.MealType.SNACK,
                    consumedAt = statsDate.atStartOfDay(),
                    dietFoods = mutableListOf(
                        DietFoodFixture(
                            foodId = food.id,
                            intake = 100
                        ).create()
                    )
                ).create(),
                withTransaction = true
            )
            val expectedTotalCalories = food.perServingCalories * 100 / food.servingSize
            val expectedTotalCarbohydrates = food.carbohydrates * 100 / food.servingSize
            val expectedTotalProtein = food.protein * 100 / food.servingSize
            val expectedTotalFat = food.fat * 100 / food.servingSize

            // when
            statsService.aggregateStats(statsDate)

            // then
            val stats = getAllEntities(DietStats::class.java)

            assertThat(stats)
                .hasSize(1)
                .allMatch { stat ->
                    stat.userId == user.id &&
                    stat.statsDate == statsDate &&
                    stat.totalCalories == expectedTotalCalories &&
                    stat.totalCarbohydrates == expectedTotalCarbohydrates &&
                    stat.totalProtein == expectedTotalProtein &&
                    stat.totalFat == expectedTotalFat
                }
        }
    }
}