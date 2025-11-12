package org.balanceeat.domain.diet

import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.DomainStatus.DIET_MEAL_TYPE_ALREADY_EXISTS
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.food.foodFixture
import org.balanceeat.domain.food.FoodRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@Transactional
class DietWriterTest : IntegrationTestContext() {
    @Autowired
    private lateinit var dietWriter: DietWriter

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
                foodFixture {
                    name = "바나나"
                    servingSize = 100.0
                    unit = "g"
                    carbohydrates = 27.0
                    protein = 1.3
                    fat = 0.2
                }
            )

            val food2 = foodRepository.save(
                foodFixture {
                    name = "우유"
                    servingSize = 200.0
                    unit = "ml"
                    carbohydrates = 9.1
                    protein = 6.6
                    fat = 3.2
                }
            )

            val command = dietCreateCommandFixture {
                userId = 1L
                mealType = Diet.MealType.BREAKFAST
                consumedAt = LocalDateTime.now()
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
            }

            // when
            val createdDiet = dietWriter.create(command)

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
            val alreadySavedDiet = dietRepository.save(dietFixture())
            val command = dietCreateCommandFixture {
                userId = alreadySavedDiet.userId
                mealType = alreadySavedDiet.mealType
                consumedAt = alreadySavedDiet.consumedAt
            }

            // when
            val throwable = catchThrowable { dietWriter.create(command) }

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
            val food1 = foodRepository.save(foodFixture { name = "사과" })
            val food2 = foodRepository.save(foodFixture { name = "바나나" })
            val food3 = foodRepository.save(foodFixture { name = "당근" })
            val food4 = foodRepository.save(foodFixture { name = "토마토" })

            val existingDiet = dietRepository.save(
                dietFixture {
                    dietFoods = mutableListOf(
                        dietFoodFixture {
                            foodId = food1.id
                            intake = 1
                        },
                        dietFoodFixture {
                            foodId = food2.id
                            intake = 1
                        },
                        dietFoodFixture {
                            foodId = food4.id
                            intake = 1
                        }
                    )
                }
            )

            val command = dietUpdateCommandFixture {
                id = existingDiet.id
                mealType = Diet.MealType.DINNER
                consumedAt = LocalDateTime.now()
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
            }

            // when
            val result = dietWriter.update(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }

        @Test
        fun `수정 시 같은 날짜, 같은 MealType의 다른 식단이 있으면 실패한다`() {
            // given
            val existingDiet1 = dietRepository.save(
                dietFixture {
                    mealType = Diet.MealType.BREAKFAST
                    consumedAt = LocalDateTime.now()
                }
            )

            val existingDiet2 = dietRepository.save(
                dietFixture {
                    mealType = Diet.MealType.LUNCH
                    consumedAt = LocalDateTime.now()
                }
            )

            val updateCommand = dietUpdateCommandFixture {
                id = existingDiet2.id
                mealType = Diet.MealType.BREAKFAST // 이미 존재하는 MealType으로 변경
                consumedAt = existingDiet2.consumedAt
            }

            // when
            val throwable = catchThrowable { dietWriter.update(updateCommand) }

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
            val diet = dietRepository.save(dietFixture())

            // when
            dietWriter.delete(diet.id)

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
            val food1 = foodRepository.save(foodFixture { name = "사과" })
            val food2 = foodRepository.save(foodFixture { name = "바나나" })
            val food3 = foodRepository.save(foodFixture { name = "포도" })

            val savedDiet = dietRepository.save(
                dietFixture {
                    dietFoods = mutableListOf(
                        dietFoodFixture {
                            foodId = food1.id
                            intake = 2
                        },
                        dietFoodFixture {
                            foodId = food2.id
                            intake = 3
                        },
                        dietFoodFixture {
                            foodId = food3.id
                            intake = 1
                        }
                    )
                }
            )

            // dietFoods를 미리 로드하여 ID 저장
            val dietFoodIds = savedDiet.dietFoods.map { it.id }
            val dietFoodIdToDelete = dietFoodIds[1] // 바나나 삭제

            val command = dietDeleteDietFoodCommandFixture {
                dietId = savedDiet.id
                dietFoodId = dietFoodIdToDelete
            }

            // when
            dietWriter.deleteDietFood(command)

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
            val command = dietDeleteDietFoodCommandFixture {
                dietId = 999L
                dietFoodId = 1L
            }

            // when
            val throwable = catchThrowable { dietWriter.deleteDietFood(command) }

            // then
            assertThat(throwable).isInstanceOf(org.balanceeat.domain.common.exception.EntityNotFoundException::class.java)
        }

        @Test
        fun `존재하지 않는 식단 음식 ID로 삭제 시도하면 실패한다`() {
            // given
            val food = foodRepository.save(foodFixture { name = "사과" })
            val diet = dietRepository.save(
                dietFixture {
                    dietFoods = mutableListOf(
                        dietFoodFixture {
                            foodId = food.id
                            intake = 2
                        }
                    )
                }
            )

            val command = dietDeleteDietFoodCommandFixture {
                dietId = diet.id
                dietFoodId = 999L // 존재하지 않는 dietFoodId
            }

            // when
            val throwable = catchThrowable { dietWriter.deleteDietFood(command) }

            // then
            assertThat(throwable).isInstanceOf(DomainException::class.java)
                .hasMessageContaining(DomainStatus.DIET_FOOD_NOT_FOUND.message)
        }
    }

    @Nested
    @DisplayName("식단 음식 섭취량 수정 테스트")
    inner class UpdateDietFoodTest {
        @Test
        fun `식단 음식 섭취량 수정 성공`() {
            // given
            val food = foodRepository.save(foodFixture { name = "사과" })
            val savedDiet = dietRepository.save(
                dietFixture {
                    dietFoods = mutableListOf(
                        dietFoodFixture {
                            foodId = food.id
                            intake = 100
                        }
                    )
                }
            )

            val dietFoodId = savedDiet.dietFoods.first().id
            val command = dietUpdateDietFoodCommandFixture {
                dietId = savedDiet.id
                this.dietFoodId = dietFoodId
                intake = 150
            }

            // when
            val result = dietWriter.updateDietFood(command)

            // then
            assertThat(result.id).isEqualTo(savedDiet.id)
            assertThat(result.dietFoods).hasSize(1)
            val updatedDietFood = result.dietFoods.first()
            assertThat(updatedDietFood.intake).isEqualTo(command.intake)
            assertThat(updatedDietFood.foodName).isEqualTo(food.name)
        }

        @Test
        fun `존재하지 않는 식단으로 수정 시도하면 실패한다`() {
            // given
            val command = dietUpdateDietFoodCommandFixture {
                dietId = 999L
                dietFoodId = 1L
                intake = 150
            }

            // when
            val throwable = catchThrowable { dietWriter.updateDietFood(command) }

            // then
            assertThat(throwable).isInstanceOf(org.balanceeat.domain.common.exception.EntityNotFoundException::class.java)
        }

        @Test
        fun `존재하지 않는 식단 음식 ID로 수정 시도하면 실패한다`() {
            // given
            val food = foodRepository.save(foodFixture { name = "사과" })
            val diet = dietRepository.save(
                dietFixture {
                    dietFoods = mutableListOf(
                        dietFoodFixture {
                            foodId = food.id
                            intake = 100
                        }
                    )
                }
            )

            val command = dietUpdateDietFoodCommandFixture {
                dietId = diet.id
                dietFoodId = 999L // 존재하지 않는 dietFoodId
                intake = 150
            }

            // when
            val throwable = catchThrowable { dietWriter.updateDietFood(command) }

            // then
            assertThat(throwable).isInstanceOf(DomainException::class.java)
                .hasMessageContaining(DomainStatus.DIET_FOOD_NOT_FOUND.message)
        }
    }
}
