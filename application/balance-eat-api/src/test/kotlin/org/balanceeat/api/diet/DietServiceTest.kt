package org.balanceeat.api.diet

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietFixture
import org.balanceeat.domain.diet.DietFoodFixture
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.food.FoodRepository
import org.balanceeat.domain.user.UserFixture
import org.balanceeat.domain.user.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

class DietServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var dietService: DietService

    @Autowired
    private lateinit var dietRepository: DietRepository

    @Autowired
    private lateinit var foodRepository: FoodRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        dietRepository.deleteAll()
        foodRepository.deleteAll()
        userRepository.deleteAll()
    }

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

    @Nested
    @DisplayName("일일 식단 조회 테스트")
    inner class GetDailyDietsTest {
        @Test
        fun `특정 날짜의 사용자 식단을 조회할 수 있다`() {
            // given
            val userId = 1L
            val targetDateTime = LocalDateTime.now()
            val food = createEntity(FoodFixture(name = "김치찌개").create())
            val dietFoods = mutableListOf(
                DietFoodFixture(foodId = food.id).create()
            )
            val diet = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetDateTime,
                    dietFoods = dietFoods
                ).create()
            )

            // when
            val result = dietService.getDailyDiets(userId, targetDateTime.toLocalDate())

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].dietId).isEqualTo(diet.id)
            assertThat(result[0].mealType).isEqualTo(diet.mealType)
            assertThat(result[0].consumedAt).isEqualTo(diet.consumedAt)
            assertThat(result[0].items).hasSize(1)

            // 음식 정보 검증
            val foodItem = result[0].items[0]
            val dietFood = dietFoods[0]
            assertThat(foodItem.foodId).isEqualTo(dietFood.foodId)
            assertThat(foodItem.foodName).isEqualTo(food.name)
            assertThat(foodItem.intake).isEqualTo(dietFood.intake)
        }

        // 식단이 여러개 있을때 먹은 시간 오름차순으로 정렬되는지 검증
        @Test
        fun `식단이 여러개 있을때 먹은 시간 오름차순으로 정렬된다`() {
            // given
            val userId = 1L
            val targetDate = LocalDate.now()
            val targetDateTime = LocalDateTime.now()
            val food1 = createEntity(FoodFixture(name = "김치찌개").create())
            val food2 = createEntity(FoodFixture(name = "된장찌개").create())
            val diet1 = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetDateTime.withHour(20),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food1.id).create())
                ).create()
            )
            val diet2 = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetDateTime.withHour(8),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food2.id).create())
                ).create()
            )

            // when
            val result = dietService.getDailyDiets(userId, targetDate)

            // then
            assertThat(result).hasSize(2)
            assertThat(result[0].dietId).isEqualTo(diet2.id)
            assertThat(result[1].dietId).isEqualTo(diet1.id)
        }
    }
}