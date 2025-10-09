package org.balanceeat.api.diet

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.ApplicationStatus.CANNOT_MODIFY_DIET
import org.balanceeat.apibase.ApplicationStatus.CANNOT_MODIFY_FOOD
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.DietFixture
import org.balanceeat.domain.diet.DietFoodFixture
import org.balanceeat.domain.diet.DietRepository
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.user.UserFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class DietServiceTest : IntegrationTestContext() {
    @Autowired
    private lateinit var dietService: DietService
    @Autowired
    private lateinit var dietRepository: DietRepository

    @Nested
    @DisplayName("식단 생성 테스트")
    inner class CreateTest {
        @Test
        fun `식단을 성공적으로 생성할 수 있다`() {
            // given
            val user = createEntity(UserFixture().create())
            val food1 = createEntity(FoodFixture(
                name = "토스트",
                servingSize = 30.0,
                unit = "g",
                carbohydrates = 15.0,
                protein = 2.5,
                fat = 1.5
            ).create())
            
            val food2 = createEntity(FoodFixture(
                name = "우유",
                servingSize = 200.0,
                unit = "ml",
                carbohydrates = 9.0,
                protein = 6.6,
                fat = 3.2
            ).create())
            
            val food3 = createEntity(FoodFixture(
                name = "바나나",
                servingSize = 100.0,
                unit = "g",
                carbohydrates = 27.0,
                protein = 1.3,
                fat = 0.2
            ).create())
            
            val request = DietV1RequestFixture.Create(
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
            ).create()

            // when
            val result = dietService.create(request, user.id)

            // then
            assertThat(request)
                .usingRecursiveComparison()
                .isEqualTo(result)

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
    @Transactional
    @DisplayName("일일 식단 조회 테스트")
    open inner class GetDailyDietsTest {
        @Test
        fun `특정 날짜의 사용자 식단을 조회할 수 있다`() {
            // given
            val userId = 1L
            val targetDateTime = LocalDateTime.now()
            val food = createEntity(FoodFixture(name = "김치찌개").create(), withTransaction = true)
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

        @Test
        fun `식단이 여러개 있을때 먹은 시간 오름차순으로 정렬된다`() {
            // given
            val userId = 1L
            val targetDate = LocalDate.now()
            val targetDateTime = LocalDateTime.now()
            val food1 = createEntity(FoodFixture(name = "김치찌개").create(), withTransaction = true)
            val food2 = createEntity(FoodFixture(name = "된장찌개").create(), withTransaction = true)
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

    @Nested
    @Transactional
    @DisplayName("월별 식단 조회 테스트")
    open inner class GetMonthlyDietsTest {
        @Test
        fun `특정 월의 사용자 식단을 조회할 수 있다`() {
            // given
            val userId = 1L
            val targetYearMonth = YearMonth.of(2024, 3)
            val targetDateTime = targetYearMonth.atDay(15).atTime(12, 0)
            val food = createEntity(FoodFixture(name = "김치찌개").create(), withTransaction = true)
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
            val result = dietService.getMonthlyDiets(userId, targetYearMonth)

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

        @Test
        fun `같은 월에 있는 여러 식단을 시간 오름차순으로 정렬한다`() {
            // given
            val userId = 1L
            val targetYearMonth = YearMonth.of(2024, 3)
            val food1 = createEntity(FoodFixture(name = "김치찌개").create(), withTransaction = true)
            val food2 = createEntity(FoodFixture(name = "된장찌개").create(), withTransaction = true)
            val diet1 = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetYearMonth.atDay(20).atTime(20, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food1.id).create())
                ).create()
            )
            val diet2 = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetYearMonth.atDay(10).atTime(8, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food2.id).create())
                ).create()
            )

            // when
            val result = dietService.getMonthlyDiets(userId, targetYearMonth)

            // then
            assertThat(result).hasSize(2)
            assertThat(result[0].dietId).isEqualTo(diet2.id) // 3월 10일이 먼저
            assertThat(result[1].dietId).isEqualTo(diet1.id) // 3월 20일이 나중
        }

        @Test
        fun `다른 월의 식단은 조회되지 않는다`() {
            // given
            val userId = 1L
            val targetYearMonth = YearMonth.of(2024, 3)
            val food = createEntity(FoodFixture(name = "김치찌개").create(), withTransaction = true)

            // 2월 데이터 생성
            dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = YearMonth.of(2024, 2).atDay(15).atTime(12, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food.id).create())
                ).create()
            )

            // 4월 데이터 생성
            dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = YearMonth.of(2024, 4).atDay(15).atTime(12, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food.id).create())
                ).create()
            )

            // 3월 데이터 생성
            val targetDiet = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetYearMonth.atDay(15).atTime(12, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food.id).create())
                ).create()
            )

            // when
            val result = dietService.getMonthlyDiets(userId, targetYearMonth)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].dietId).isEqualTo(targetDiet.id)
        }

        @Test
        fun `다른 사용자의 식단은 조회되지 않는다`() {
            // given
            val userId = 1L
            val otherUserId = 2L
            val targetYearMonth = YearMonth.of(2024, 3)
            val food = createEntity(FoodFixture(name = "김치찌개").create(), withTransaction = true)

            // 다른 사용자의 데이터 생성
            dietRepository.save(
                DietFixture(
                    userId = otherUserId,
                    consumedAt = targetYearMonth.atDay(15).atTime(12, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food.id).create())
                ).create()
            )

            // 해당 사용자의 데이터 생성
            val targetDiet = dietRepository.save(
                DietFixture(
                    userId = userId,
                    consumedAt = targetYearMonth.atDay(15).atTime(12, 0),
                    dietFoods = mutableListOf(DietFoodFixture(foodId = food.id).create())
                ).create()
            )

            // when
            val result = dietService.getMonthlyDiets(userId, targetYearMonth)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].dietId).isEqualTo(targetDiet.id)
        }
    }

    @Nested
    @DisplayName("식단 수정 테스트")
    inner class UpdateTest {
        @Test
        fun `식단을 성공적으로 수정할 수 있다`() {
            // given
            val user = createEntity(UserFixture().create())
            val food1 = createEntity(FoodFixture(name = "사과").create())
            val food2 = createEntity(FoodFixture(name = "치킨").create())
            val food3 = createEntity(FoodFixture(name = "피자").create())

            val existingDiet = dietRepository.save(
                DietFixture(
                    userId = user.id,
                    mealType = Diet.MealType.BREAKFAST,
                    dietFoods = mutableListOf(
                        DietFoodFixture(foodId = food1.id, intake = 1).create(),
                        DietFoodFixture(foodId = food2.id, intake = 2).create(),
                        DietFoodFixture(foodId = food3.id, intake = 3).create()
                    )
                ).create()
            )

            val request = DietV1RequestFixture.Update(
                mealType = Diet.MealType.DINNER,
                consumedAt = LocalDateTime.now(),
                dietFoods = listOf(
                    DietV1Request.Update.DietFood(foodId = food1.id, intake = 4),
                    DietV1Request.Update.DietFood(foodId = food2.id, intake = 5)
                )
            ).create()

            // when
            val result = dietService.update(request,existingDiet.id, user.id)

            // then
            assertThat(request)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }

    @Test
    fun `다른 사용자의 식단을 수정하려 하면 실패한다`() {
        // given
        val user = createEntity(UserFixture().create())
        val otherUser = createEntity(UserFixture(name = "otherUser").create())
        val existingDiet = dietRepository.save(
            DietFixture(userId = user.id).create()
        )

        val request = DietV1RequestFixture.Update().create()

        // when
        val throwable = catchThrowable { dietService.update(request, existingDiet.id, otherUser.id) }

        // then
        assertThat(throwable).isInstanceOf(BadRequestException::class.java)
            .hasFieldOrPropertyWithValue("status", CANNOT_MODIFY_FOOD)
            .hasMessage(CANNOT_MODIFY_FOOD.message)
    }

    @Nested
    @DisplayName("식단 삭제 테스트")
    inner class DeleteTest {
        @Test
        fun `식단을 성공적으로 삭제할 수 있다`() {
            // given
            val user = createEntity(UserFixture().create())
            val existingDiet = dietRepository.save(
                DietFixture(userId = user.id).create()
            )

            // when
            dietService.delete(existingDiet.id, user.id)

            // then
            assertThat(dietRepository.existsById(existingDiet.id)).isFalse()
        }

        @Test
        fun `존재하지 않는 식단을 삭제해도 예외가 발생하지 않는다`() {
            // given
            val user = createEntity(UserFixture().create())
            val nonExistentDietId = 999L

            // when
            val throwable = catchThrowable { dietService.delete(nonExistentDietId, user.id) }

            // then
            assertThat(throwable).isNull()
        }

        @Test
        fun `다른 사용자의 식단을 삭제하려 하면 실패한다`() {
            // given
            val user = createEntity(UserFixture().create())
            val otherUser = createEntity(UserFixture(name = "otherUser").create())
            val existingDiet = dietRepository.save(
                DietFixture(userId = user.id).create()
            )

            // when
            val throwable = catchThrowable { dietService.delete(existingDiet.id, otherUser.id) }

            // then
            assertThat(throwable).isInstanceOf(BadRequestException::class.java)
                .hasFieldOrPropertyWithValue("status", CANNOT_MODIFY_DIET)
                .hasMessage(CANNOT_MODIFY_DIET.message)
        }
    }
}