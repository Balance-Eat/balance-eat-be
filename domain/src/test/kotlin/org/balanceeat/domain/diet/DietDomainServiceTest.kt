package org.balanceeat.domain.diet

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.domain.common.DomainStatus.DIET_MEAL_TYPE_ALREADY_EXISTS
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.food.FoodFixture
import org.balanceeat.domain.food.FoodRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@Transactional
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
            val food1 = foodRepository.save(
                FoodFixture(
                    name = "바나나",
                    servingSize = 100.0,
                    unit = "g",
                    carbohydrates = 27.0,
                    protein = 1.3,
                    fat = 0.2
                ).create()
            )

            val food2 = foodRepository.save(
                FoodFixture(
                    name = "우유",
                    servingSize = 200.0,
                    unit = "ml",
                    carbohydrates = 9.1,
                    protein = 6.6,
                    fat = 3.2
                ).create()
            )

            val command = DietCommand.Create(
                userId = 1L,
                mealType = Diet.MealType.BREAKFAST,
                consumedAt = LocalDateTime.now(),
                dietFoods = listOf(
                    DietCommand.Create.DietFood(
                        foodId = food1.id,
                        intake = 2 // 바나나 2개
                    ),
                    DietCommand.Create.DietFood(
                        foodId = food2.id,
                        intake = 1 // 우유 1개
                    )
                )
            )

            // when
            val createdDiet = dietDomainService.create(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(createdDiet)

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
        }

        @Test
        fun `같은 날짜, 같은 사용자, 같은 MealType으로 식단을 생성하면 실패한다`() {
            // given
            val alreadySavedDiet = dietRepository.save(DietFixture().create())
            val command = DietCommandFixture.Create(
                userId = alreadySavedDiet.userId,
                mealType = alreadySavedDiet.mealType,
                consumedAt = alreadySavedDiet.consumedAt,
            ).create()

            // when
            val throwable = catchThrowable { dietDomainService.create(command) }

            // then
            assertThat(throwable).isInstanceOf(DomainException::class.java)
                .hasFieldOrPropertyWithValue("status", DIET_MEAL_TYPE_ALREADY_EXISTS)
                .hasMessage(DIET_MEAL_TYPE_ALREADY_EXISTS.message)
        }
    }

    @Nested
    @DisplayName("식단 수정 테스트")
    inner class UpdateTest {
        @Test
        fun `식단 수정 성공`() {
            // given
            val food1 = foodRepository.save(FoodFixture(name = "사과").create())
            val food2 = foodRepository.save(FoodFixture(name = "바나나").create())
            val food3 = foodRepository.save(FoodFixture(name = "당근").create())
            val food4 = foodRepository.save(FoodFixture(name = "토마토").create())

            val existingDiet = dietRepository.save(
                DietFixture(
                    dietFoods = mutableListOf(
                        DietFoodFixture(foodId = food1.id, intake = 1).create(),
                        DietFoodFixture(foodId = food2.id, intake = 1).create(),
                        DietFoodFixture(foodId = food4.id, intake = 1).create(),
                    )
                ).create()
            )

            val command = DietCommandFixture.Update(
                id = existingDiet.id,
                mealType = Diet.MealType.DINNER,
                consumedAt = LocalDateTime.now(),
                dietFoods = listOf(
                    DietCommand.Update.DietFood(
                        foodId = food1.id,
                        intake = 10
                    ),
                    DietCommand.Update.DietFood(
                        foodId = food2.id,
                        intake = 10
                    ),
                    DietCommand.Update.DietFood(
                        foodId = food3.id,
                        intake = 1
                    )
                )
            ).create()

            // when
            val result = dietDomainService.update(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }

        @Test
        fun `수정 시 같은 날짜, 같은 MealType의 다른 식단이 있으면 실패한다`() {
            // given
            val existingDiet1 = dietRepository.save(
                DietFixture(
                    mealType = Diet.MealType.BREAKFAST,
                    consumedAt = LocalDateTime.now()
                ).create()
            )

            val existingDiet2 = dietRepository.save(
                DietFixture(
                    mealType = Diet.MealType.LUNCH,
                    consumedAt = LocalDateTime.now()
                ).create()
            )

            val updateCommand = DietCommandFixture.Update(
                id = existingDiet2.id,
                mealType = Diet.MealType.BREAKFAST, // 이미 존재하는 MealType으로 변경
                consumedAt = existingDiet2.consumedAt,
            ).create()

            // when
            val throwable = catchThrowable { dietDomainService.update(updateCommand) }

            // then
            assertThat(throwable).isInstanceOf(DomainException::class.java)
                .hasFieldOrPropertyWithValue("status", DIET_MEAL_TYPE_ALREADY_EXISTS)
                .hasMessage(DIET_MEAL_TYPE_ALREADY_EXISTS.message)
        }
    }

    @Nested
    @DisplayName("식단 삭제 테스트")
    inner class DeleteTest {
        @Test
        fun `성공`() {
            // given
            val diet = dietRepository.save(DietFixture().create())

            // when
            dietDomainService.delete(diet.id)

            // then
            assertThat(dietRepository.existsById(diet.id)).isFalse()
        }
    }

    @Nested
    @DisplayName("식단 음식 삭제 테스트")
    inner class DeleteDietFoodTest {
        @Test
        fun `식단 음식 삭제 성공`() {
            // given
            val food1 = foodRepository.save(FoodFixture(name = "사과").create())
            val food2 = foodRepository.save(FoodFixture(name = "바나나").create())
            val food3 = foodRepository.save(FoodFixture(name = "포도").create())

            val savedDiet = dietRepository.save(
                DietFixture(
                    dietFoods = mutableListOf(
                        DietFoodFixture(foodId = food1.id, intake = 2).create(),
                        DietFoodFixture(foodId = food2.id, intake = 3).create(),
                        DietFoodFixture(foodId = food3.id, intake = 1).create()
                    )
                ).create()
            )

            // dietFoods를 미리 로드하여 ID 저장
            val dietFoodIds = savedDiet.dietFoods.map { it.id }
            val dietFoodIdToDelete = dietFoodIds[1] // 바나나 삭제

            val command = DietCommandFixture.DeleteDietFood(
                dietId = savedDiet.id,
                dietFoodId = dietFoodIdToDelete
            ).create()

            // when
            dietDomainService.deleteDietFood(command)

            // then
            val updatedDiet = dietRepository.findById(savedDiet.id).get()
            assertThat(updatedDiet.dietFoods).hasSize(2)
            assertThat(updatedDiet.dietFoods.map { it.id })
                .doesNotContain(dietFoodIdToDelete)
            assertThat(updatedDiet.dietFoods.map { it.foodId })
                .containsExactlyInAnyOrder(food1.id, food3.id)
        }

        @Test
        fun `존재하지 않는 식단으로 삭제 시도하면 실패한다`() {
            // given
            val command = DietCommandFixture.DeleteDietFood(
                dietId = 999L,
                dietFoodId = 1L
            ).create()

            // when
            val throwable = catchThrowable { dietDomainService.deleteDietFood(command) }

            // then
            assertThat(throwable).isInstanceOf(org.balanceeat.domain.common.exception.EntityNotFoundException::class.java)
        }

        @Test
        fun `존재하지 않는 식단 음식 ID로 삭제 시도하면 실패한다`() {
            // given
            val food = foodRepository.save(FoodFixture(name = "사과").create())
            val diet = dietRepository.save(
                DietFixture(
                    dietFoods = mutableListOf(
                        DietFoodFixture(foodId = food.id, intake = 2).create()
                    )
                ).create()
            )

            val command = DietCommandFixture.DeleteDietFood(
                dietId = diet.id,
                dietFoodId = 999L // 존재하지 않는 dietFoodId
            ).create()

            // when
            val throwable = catchThrowable { dietDomainService.deleteDietFood(command) }

            // then
            assertThat(throwable).isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("식단 음식을 찾을 수 없습니다")
        }
    }
}