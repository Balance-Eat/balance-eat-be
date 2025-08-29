package org.balanceeat.domain.food

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.balanceeat.domain.common.exceptions.NotFoundException
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
            assertThat(createdFood.userId).isEqualTo(command.userId)
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
                userId = 1L,
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
            assertThat(foundFood.userId).isEqualTo(food.userId)
            assertThat(foundFood.perCapitaIntake).isEqualTo(food.perCapitaIntake)
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
        fun `음식 작성자는 음식을 수정할 수 있다`() {
            // given
            val originalUserId = 1L
            val food = foodRepository.save(FoodFixture(
                name = "수정 전 음식",
                userId = originalUserId,
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 20.0,
                protein = 5.0,
                fat = 2.0,
                isVerified = false
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                userId = originalUserId,
                name = "수정 후 음식",
                perCapitaIntake = 150.0,
                carbohydrates = 30.0,
                isVerified = true
            ).create()

            // when
            val updatedFood = foodDomainService.update(updateCommand)

            // then
            assertThat(updatedFood.id).isEqualTo(food.id)
            assertThat(updatedFood.name).isEqualTo("수정 후 음식")
            assertThat(updatedFood.userId).isEqualTo(originalUserId)
            assertThat(updatedFood.perCapitaIntake).isEqualTo(150.0)
            assertThat(updatedFood.unit).isEqualTo("g") // 변경되지 않은 필드
            assertThat(updatedFood.carbohydrates).isEqualTo(30.0)
            assertThat(updatedFood.protein).isEqualTo(5.0) // 변경되지 않은 필드
            assertThat(updatedFood.fat).isEqualTo(2.0) // 변경되지 않은 필드
            assertThat(updatedFood.isVerified).isEqualTo(true)
        }

        @Test
        fun `음식 작성자가 아닌 사용자는 음식을 수정할 수 없다`() {
            // given
            val originalUserId = 1L
            val differentUserId = 2L
            val food = foodRepository.save(FoodFixture(
                name = "권한 테스트 음식",
                userId = originalUserId,
                perCapitaIntake = 100.0
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                userId = differentUserId,
                name = "권한 없는 사용자의 수정 시도"
            ).create()

            // when & then
            assertThatThrownBy {
                foodDomainService.update(updateCommand)
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("음식을 수정할 권한이 없습니다")
        }

        @Test
        fun `일부 필드만 수정할 수 있다`() {
            // given
            val userId = 1L
            val food = foodRepository.save(FoodFixture(
                name = "부분 수정 테스트",
                userId = userId,
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 20.0,
                protein = 5.0,
                fat = 2.0,
                isVerified = false
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                userId = userId,
                name = "새로운 이름만 변경"
                // 다른 필드들은 null로 두어 변경하지 않음
            ).create()

            // when
            val updatedFood = foodDomainService.update(updateCommand)

            // then
            assertThat(updatedFood.name).isEqualTo("새로운 이름만 변경")
            assertThat(updatedFood.perCapitaIntake).isEqualTo(100.0) // 기존 값 유지
            assertThat(updatedFood.unit).isEqualTo("g") // 기존 값 유지
            assertThat(updatedFood.carbohydrates).isEqualTo(20.0) // 기존 값 유지
            assertThat(updatedFood.protein).isEqualTo(5.0) // 기존 값 유지
            assertThat(updatedFood.fat).isEqualTo(2.0) // 기존 값 유지
            assertThat(updatedFood.isVerified).isEqualTo(false) // 기존 값 유지
        }
    }
}