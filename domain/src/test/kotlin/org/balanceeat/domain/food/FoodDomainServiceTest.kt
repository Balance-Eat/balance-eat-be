package org.balanceeat.domain.food

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.common.utils.CalorieCalculator
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FoodDomainServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var foodDomainService: FoodDomainService

    @Autowired
    private lateinit var foodRepository: FoodRepository

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {
        
        @Test
        fun `음식을 생성할 수 있다`() {
            // given
            val command = foodCreateCommandFixture()

            // when
            val result = foodDomainService.create(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)

            assertThat(result.perServingCalories)
                .isEqualTo(CalorieCalculator.calculate(result.carbohydrates, result.protein, result.fat))
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {

        @Test
        fun `수정 성공`() {
            // given
            val food = foodRepository.save(foodFixture())

            val updateCommand = foodUpdateCommandFixture {
                foodId = food.id
                name = "수정 후 음식"
                servingSize = 150.0
                unit = "mg"
                carbohydrates = 30.0
                protein = 10.0
                fat = 11.0
            }

            // when
            val result = foodDomainService.update(updateCommand)

            // then
            assertThat(updateCommand)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }

        @Test
        fun `일부 필드만 수정할 수 있다`() {
            // given
            val userId = 1L
            val food = foodRepository.save(foodFixture())

            val updateCommand = foodUpdateCommandFixture {
                foodId = food.id
                name = "새로운 이름만 변경"
                unit = food.unit
                servingSize = food.servingSize
                carbohydrates = 30.0
                protein = 10.0
                fat = food.fat
            }

            // when
            val result = foodDomainService.update(updateCommand)

            // then
            assertThat(updateCommand)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }
}