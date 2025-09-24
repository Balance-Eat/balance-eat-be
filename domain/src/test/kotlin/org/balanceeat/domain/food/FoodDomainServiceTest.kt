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
                userId = 1L,
                servingSize = 100.0,
                unit = "g",
                carbohydrates = 25.0,
                protein = 8.0,
                fat = 3.0,
                isAdminApproved = true
            ).create()

            // when
            val createdFood = foodDomainService.create(command)

            // then
            assertThat(createdFood.id).isNotNull()
            assertThat(createdFood.name).isEqualTo(command.name)
            assertThat(createdFood.userId).isEqualTo(command.userId)
            assertThat(createdFood.servingSize).isEqualTo(command.servingSize)
            assertThat(createdFood.unit).isEqualTo(command.unit)
            assertThat(createdFood.carbohydrates).isEqualTo(command.carbohydrates)
            assertThat(createdFood.protein).isEqualTo(command.protein)
            assertThat(createdFood.fat).isEqualTo(command.fat)
            assertThat(createdFood.isAdminApproved).isEqualTo(command.isAdminApproved)
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
                userId = 1L,
                servingSize = 200.0,
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
            assertThat(foundFood.userId).isEqualTo(food.userId)
            assertThat(foundFood.servingSize).isEqualTo(food.servingSize)
            assertThat(foundFood.unit).isEqualTo(food.unit)
            assertThat(foundFood.carbohydrates).isEqualTo(food.carbohydrates)
            assertThat(foundFood.protein).isEqualTo(food.protein)
            assertThat(foundFood.fat).isEqualTo(food.fat)
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {

        @Test
        fun `수정 성공`() {
            // given
            val food = foodRepository.save(FoodFixture(
                name = "수정 전 음식",
                servingSize = 100.0,
                unit = "g",
                carbohydrates = 20.0,
                protein = 5.0,
                fat = 2.0,
                isAdminApproved = false
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                name = "수정 후 음식",
                servingSize = 150.0,
                unit = "mg",
                carbohydrates = 30.0,
                protein = 10.0,
                fat = 11.0
            ).create()

            // when
            val updatedFood = foodDomainService.update(updateCommand)

            // then
            assertThat(updatedFood.id).isEqualTo(food.id)
            assertThat(updatedFood.name).isEqualTo("수정 후 음식")
            assertThat(updatedFood.servingSize).isEqualTo(150.0)
            assertThat(updatedFood.unit).isEqualTo("mg")
            assertThat(updatedFood.carbohydrates).isEqualTo(30.0)
            assertThat(updatedFood.protein).isEqualTo(10.0)
            assertThat(updatedFood.fat).isEqualTo(11.0)
            assertThat(updatedFood.isAdminApproved).isEqualTo(false) // 수정 시 검수 상태 유지
        }

        @Test
        fun `일부 필드만 수정할 수 있다`() {
            // given
            val userId = 1L
            val food = foodRepository.save(FoodFixture(
                name = "부분 수정 테스트",
                userId = userId,
                servingSize = 100.0,
                unit = "g",
                carbohydrates = 20.0,
                protein = 5.0,
                fat = 2.0,
                isAdminApproved = false
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                name = "새로운 이름만 변경",
                unit = food.unit,
                servingSize = food.servingSize,
                carbohydrates = 30.0,
                protein = 10.0,
                fat = food.fat
            ).create()

            // when
            val updatedFood = foodDomainService.update(updateCommand)

            // then
            assertThat(updatedFood.name).isEqualTo("새로운 이름만 변경")
            assertThat(updatedFood.unit).isEqualTo(food.unit)
            assertThat(updatedFood.servingSize).isEqualTo(food.servingSize)
            assertThat(updatedFood.carbohydrates).isEqualTo(30.0)
            assertThat(updatedFood.protein).isEqualTo(10.0)
            assertThat(updatedFood.fat).isEqualTo(food.fat)
            assertThat(updatedFood.isAdminApproved).isEqualTo(false) // 수정 시 검수 상태 유지
        }
    }
}