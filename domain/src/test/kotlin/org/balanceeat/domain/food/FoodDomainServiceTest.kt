package org.balanceeat.domain.food

import org.assertj.core.api.Assertions.assertThat
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
            val command = FoodCommandFixture.Create(
                name = "테스트 음식",
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 25.0,
                protein = 8.0,
                fat = 3.0,
                isVerified = true
            ).create()

            // when
            val createdFood = foodDomainService.create(command)

            // then
            assertThat(createdFood.id).isNotNull()
            assertThat(createdFood.name).isEqualTo(command.name)
            assertThat(createdFood.perCapitaIntake).isEqualTo(command.perCapitaIntake)
            assertThat(createdFood.unit).isEqualTo(command.unit)
            assertThat(createdFood.carbohydrates).isEqualTo(command.carbohydrates)
            assertThat(createdFood.protein).isEqualTo(command.protein)
            assertThat(createdFood.fat).isEqualTo(command.fat)
            assertThat(createdFood.isVerified).isEqualTo(command.isVerified)
            assertThat(createdFood.uuid).isNotBlank()
        }
    }

    @Nested
    @DisplayName("조회 테스트")
    inner class GetFoodTest {
        
        @Test
        fun `음식 정보를 조회할 수 있다`() {
            // given
            val food = foodRepository.save(FoodFixture(
                name = "조회 테스트 음식",
                perCapitaIntake = 200.0,
                unit = "g",
                carbohydrates = 45.0,
                protein = 12.0,
                fat = 8.0
            ).create())

            // when
            val foundFood = foodDomainService.getFood(food.id)

            // then
            assertThat(foundFood.id).isEqualTo(food.id)
            assertThat(foundFood.name).isEqualTo(food.name)
            assertThat(foundFood.uuid).isEqualTo(food.uuid)
            assertThat(foundFood.perCapitaIntake).isEqualTo(food.perCapitaIntake)
            assertThat(foundFood.unit).isEqualTo(food.unit)
            assertThat(foundFood.carbohydrates).isEqualTo(food.carbohydrates)
            assertThat(foundFood.protein).isEqualTo(food.protein)
            assertThat(foundFood.fat).isEqualTo(food.fat)
        }
    }
}