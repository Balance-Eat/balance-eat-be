package org.balanceeat.api.stats

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietFixture
import org.balanceeat.domain.diet.DietFoodFixture
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.diet.StatsType
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.stats.DietStats
import org.balanceeat.domain.stats.DietStatsFixture
import org.balanceeat.domain.stats.DietStatsRepository
import org.balanceeat.domain.user.UserFixture
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

class StatsServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var statsService: StatsService
    @Autowired
    private lateinit var dietRepository: DietRepository
    @Autowired
    private lateinit var dietStatsRepository: DietStatsRepository

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

            val diet = dietRepository.save(
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
                ).create()
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

        @Test
        fun `집계할 식단 데이터가 없으면 0으로 비어있는 통계데이터가 생성된다`() {
            // given
            val statsDate = LocalDate.now()
            val user1 = createEntity(UserFixture().create())
            val user2 = createEntity(UserFixture().create())

            // when
            statsService.aggregateStats(statsDate)

            // then
            val stats = getAllEntities(DietStats::class.java)

            assertThat(stats)
                .hasSize(2)
                .allMatch { stat ->
                    stat.userId != 0L &&
                    stat.statsDate == statsDate &&
                    stat.totalCalories == 0.0 &&
                    stat.totalCarbohydrates == 0.0 &&
                    stat.totalProtein == 0.0 &&
                    stat.totalFat == 0.0
                }
        }
    }

    @Transactional
    @Nested
    inner class GetStatsTest {
        @Test
        fun `데이터가 없으면 날짜별로 빈값 반환`() {
            //given
            val limit = 5

            // when
            val result = statsService.getStats(
                userId = 1L,
                type = StatsType.DAILY,
                from = LocalDate.now().minusDays(limit - 1L),
                to = LocalDate.now()
            )

            // then
            assertThat(result)
                .hasSize(limit)
                .allMatch { stat ->
                    stat.type == StatsType.DAILY &&
                    stat.totalCalories == 0.0 &&
                    stat.totalCarbohydrates == 0.0 &&
                    stat.totalProtein == 0.0 &&
                    stat.totalFat == 0.0
                }
        }

        @Test
        fun `일단위 통계 조회 - 최근 N일 데이터 조회`() {
            // given
            val limit = 5
            val today = LocalDate.now()

            // 최근 5일간의 통계 데이터 생성
            val statsList = (0..limit - 1).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                DietStatsFixture(
                    userId = 1L,
                    statsDate = date,
                    totalCalories = 2000.0 + daysAgo * 100,
                    totalCarbohydrates = 250.0 + daysAgo * 10,
                    totalProtein = 100.0 + daysAgo * 5,
                    totalFat = 60.0 + daysAgo * 3,
                ).create()
            }.reversed() // 날짜 오름차순 정렬을 위해 역순으로 변경
            dietStatsRepository.createAll(statsList)

            // when
            val result = statsService.getStats(
                userId = 1L,
                type = StatsType.DAILY,
                from = LocalDate.now().minusDays(limit - 1L),
                to = LocalDate.now(),
            )

            // then
            assertThat(result).hasSize(limit)
            result.forEachIndexed { index, stat ->
                val expected = statsList[index]
                assertThat(stat.type).isEqualTo(StatsType.DAILY)
                assertThat(stat.date).isEqualTo(expected.statsDate)
                assertThat(stat.totalCalories).isEqualTo(expected.totalCalories)
                assertThat(stat.totalCarbohydrates).isEqualTo(expected.totalCarbohydrates)
                assertThat(stat.totalProtein).isEqualTo(expected.totalProtein)
                assertThat(stat.totalFat).isEqualTo(expected.totalFat)
            }
        }

        @Test
        fun `일단위 통계 조회 - limit보다 데이터가 적으면 있는 만큼만 반환`() {
            // given
            val limit = 10
            val today = LocalDate.now()

            // 3일치 데이터만 생성
            val statsList = (0..2).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                DietStatsFixture(
                    userId = 1L,
                    statsDate = date,
                    totalCalories = 2000.0,
                    totalCarbohydrates = 250.0,
                    totalProtein = 100.0,
                    totalFat = 60.0
                ).create()
            }
            dietStatsRepository.createAll(statsList)

            // when
            val result = statsService.getStats(
                userId = 1L,
                type = StatsType.DAILY,
                from = LocalDate.now().minusDays(limit - 1L),
                to = LocalDate.now(),
            )

            // then
            val emptyStatsCount = result.count { it.totalCalories == 0.0 }
            assertThat(result).hasSize(limit)
            assertThat(emptyStatsCount).isEqualTo(limit - statsList.size)
        }

        @Test
        fun `주단위 통계 조회 - 최근 4주 데이터 조회`() {
            // given
            val limit = 5
            val requestDate = LocalDate.of(2025, 11, 5) // 수요일

            // 요번주 (월-화) 데이터 생성
            val thisWeekMonday = requestDate.minusDays(2)
            val statsList = mutableListOf<DietStats>()
            (0..1).forEach { dayOffset ->
                statsList.add(
                    DietStatsFixture(
                        userId = 1L,
                        statsDate = thisWeekMonday.plusDays(dayOffset.toLong()),
                        totalCalories = 2000.0,
                        totalCarbohydrates = 250.0,
                        totalProtein = 100.0,
                        totalFat = 60.0
                    ).create()
                )
            }

            // 지난주 (월-금) - 7일 전부터
            val lastWeekMonday = thisWeekMonday.minusWeeks(1)
            (0..4).forEach { dayOffset ->
                statsList.add(
                    DietStatsFixture(
                        userId = 1L,
                        statsDate = lastWeekMonday.plusDays(dayOffset.toLong()),
                        totalCalories = 2000.0,
                        totalCarbohydrates = 250.0,
                        totalProtein = 100.0,
                        totalFat = 60.0
                    ).create()
                )
            }

            // 지지난주 (월-금) - 21일 전부터
            val threeWeeksAgoMonday = thisWeekMonday.minusWeeks(3)
            (0..4).forEach { dayOffset ->
                statsList.add(
                    DietStatsFixture(
                        userId = 1L,
                        statsDate = threeWeeksAgoMonday.plusDays(dayOffset.toLong()),
                        totalCalories = 2000.0,
                        totalCarbohydrates = 250.0,
                        totalProtein = 100.0,
                        totalFat = 60.0
                    ).create()
                )
            }
            dietStatsRepository.createAll(statsList)

            // when
            val result = statsService.getStats(
                userId = 1L,
                type = StatsType.WEEKLY,
                from = requestDate.minusWeeks(limit - 1L),
                to = requestDate,
            )

            // then
            assertThat(result.size).isEqualTo(limit)
            // 가장 최근 주차 데이터 검증
            val lastElement = result[result.size - 1]
            // 각 주별로 5일치 데이터가 합산되어야 함
            assertThat(lastElement.type).isEqualTo(StatsType.WEEKLY)
            assertThat(lastElement.date).isEqualTo(thisWeekMonday)
            assertThat(lastElement.totalCalories).isEqualTo(2000.0 * 2)
            assertThat(lastElement.totalCarbohydrates).isEqualTo(250.0 * 2)
            assertThat(lastElement.totalProtein).isEqualTo(100.0 * 2)
            assertThat(lastElement.totalFat).isEqualTo(60.0 * 2)
        }

        @Test
        fun `월단위 통계 조회 - 최근 3개월 데이터 조회`() {
            // given
            val limit = 5
            val today = LocalDate.of(2025, 11, 11)
            val statsList = mutableListOf<DietStats>()

            // 이번 달 (1-10일)
            val thisMonthStart = today.withDayOfMonth(1)
            (0..9).forEach { dayOffset ->
                statsList.add(
                    DietStatsFixture(
                        userId = 1L,
                        statsDate = thisMonthStart.plusDays(dayOffset.toLong()),
                        totalCalories = 2000.0,
                        totalCarbohydrates = 250.0,
                        totalProtein = 100.0,
                        totalFat = 60.0
                    ).create()
                )
            }

            // 저번 달 (1-30일)
            val oneMonthAgoStart = today.minusMonths(1).withDayOfMonth(1)
            (0..29).forEach { dayOffset ->
                statsList.add(
                    DietStatsFixture(
                        userId = 1L,
                        statsDate = oneMonthAgoStart.plusDays(dayOffset.toLong()),
                        totalCalories = 2000.0,
                        totalCarbohydrates = 250.0,
                        totalProtein = 100.0,
                        totalFat = 60.0
                    ).create()
                )
            }

            // 저저번 달 (1-20일)
            val twoMonthsAgoStart = today.minusMonths(2).withDayOfMonth(1)
            (0..19).forEach { dayOffset ->
                statsList.add(
                    DietStatsFixture(
                        userId = 1L,
                        statsDate = twoMonthsAgoStart.plusDays(dayOffset.toLong()),
                        totalCalories = 2000.0,
                        totalCarbohydrates = 250.0,
                        totalProtein = 100.0,
                        totalFat = 60.0
                    ).create()
                )
            }
            dietStatsRepository.createAll(statsList)

            // when
            val result = statsService.getStats(
                userId = 1L,
                type = StatsType.MONTHLY,
                from = today.minusMonths(limit - 1L),
                to = today,
            )

            // then
            assertThat(result.size).isEqualTo(limit)
            // 각 월별로 10일치 데이터가 합산되어야 함
            val twoMonthAgoStats = result.find { it.date == twoMonthsAgoStart }!!
            assertThat(twoMonthAgoStats.type).isEqualTo(StatsType.MONTHLY)
            assertThat(twoMonthAgoStats.totalCalories).isEqualTo(2000.0 * 20)
            assertThat(twoMonthAgoStats.totalCarbohydrates).isEqualTo(250.0 * 20)
            assertThat(twoMonthAgoStats.totalProtein).isEqualTo(100.0 * 20)
            assertThat(twoMonthAgoStats.totalFat).isEqualTo(60.0 * 20)
        }
    }
}