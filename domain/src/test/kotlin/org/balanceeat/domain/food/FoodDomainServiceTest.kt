package org.balanceeat.domain.food

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.BadCommandException
import org.balanceeat.domain.common.exception.EntityNotFoundException
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
                perCapitaIntake = 100.0,
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
            assertThat(createdFood.perCapitaIntake).isEqualTo(command.perCapitaIntake)
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
                isAdminApproved = false
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                modifierId = originalUserId,
                name = "수정 후 음식",
                perCapitaIntake = 150.0,
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
            assertThat(updatedFood.userId).isEqualTo(originalUserId)
            assertThat(updatedFood.perCapitaIntake).isEqualTo(150.0)
            assertThat(updatedFood.unit).isEqualTo("mg")
            assertThat(updatedFood.carbohydrates).isEqualTo(30.0)
            assertThat(updatedFood.protein).isEqualTo(10.0)
            assertThat(updatedFood.fat).isEqualTo(11.0)
            assertThat(updatedFood.isAdminApproved).isEqualTo(false) // 수정 시 검수 상태 유지
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
                modifierId = differentUserId,
                name = "권한 없는 사용자의 수정 시도"
            ).create()

            // when & then
            assertThatThrownBy {
                foodDomainService.update(updateCommand)
            }
                .isInstanceOf(BadCommandException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.CANNOT_MODIFY_FOOD)
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
                isAdminApproved = false
            ).create())

            val updateCommand = FoodCommandFixture.Update(
                foodId = food.id,
                modifierId = userId,
                name = "새로운 이름만 변경",
                unit = food.unit,
                perCapitaIntake = food.perCapitaIntake,
                carbohydrates = 30.0,
                protein = 10.0,
                fat = food.fat
            ).create()

            // when
            val updatedFood = foodDomainService.update(updateCommand)

            // then
            assertThat(updatedFood.name).isEqualTo("새로운 이름만 변경")
            assertThat(updatedFood.unit).isEqualTo(food.unit)
            assertThat(updatedFood.perCapitaIntake).isEqualTo(food.perCapitaIntake)
            assertThat(updatedFood.carbohydrates).isEqualTo(30.0)
            assertThat(updatedFood.protein).isEqualTo(10.0)
            assertThat(updatedFood.fat).isEqualTo(food.fat)
            assertThat(updatedFood.isAdminApproved).isEqualTo(false) // 수정 시 검수 상태 유지
        }
    }

    @Nested
    @DisplayName("관리자 수정 테스트")
    inner class UpdateByAdminTest {

        @Test
        fun `관리자는 모든 음식을 수정할 수 있다`() {
            // given
            val originalUserId = 1L
            val adminId = 999L
            val food = foodRepository.save(FoodFixture(
                name = "일반 사용자 음식",
                userId = originalUserId,
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 20.0,
                protein = 5.0,
                fat = 2.0,
                isAdminApproved = false
            ).create())

            val updateByAdminCommand = FoodCommandFixture.UpdateByAdmin(
                foodId = food.id,
                adminId = adminId,
                name = "관리자가 수정한 음식",
                perCapitaIntake = 200.0,
                unit = "ml",
                carbohydrates = 40.0,
                protein = 10.0,
                fat = 5.0,
                isAdminApproved = true
            ).create()

            // when
            val updatedFood = foodDomainService.updateByAdmin(updateByAdminCommand)

            // then
            assertThat(updatedFood.id).isEqualTo(food.id)
            assertThat(updatedFood.name).isEqualTo("관리자가 수정한 음식")
            assertThat(updatedFood.userId).isEqualTo(originalUserId) // 원래 작성자 유지
            assertThat(updatedFood.perCapitaIntake).isEqualTo(200.0)
            assertThat(updatedFood.unit).isEqualTo("ml")
            assertThat(updatedFood.carbohydrates).isEqualTo(40.0)
            assertThat(updatedFood.protein).isEqualTo(10.0)
            assertThat(updatedFood.fat).isEqualTo(5.0)
            assertThat(updatedFood.isAdminApproved).isEqualTo(true)
        }

        @Test
        fun `관리자가 존재하지 않는 음식을 수정하려고 하면 예외가 발생한다`() {
            // given
            val nonExistentFoodId = 999L
            val updateByAdminCommand = FoodCommandFixture.UpdateByAdmin(
                foodId = nonExistentFoodId,
                adminId = 1L,
                name = "존재하지 않는 음식"
            ).create()

            // when & then
            assertThatThrownBy {
                foodDomainService.updateByAdmin(updateByAdminCommand)
            }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.FOOD_NOT_FOUND)
        }

        @Test
        fun `관리자는 음식을 검증된 상태로 변경할 수 있다`() {
            // given
            val food = foodRepository.save(FoodFixture(
                name = "검증 전 음식",
                userId = 1L,
                isAdminApproved = false
            ).create())

            val updateByAdminCommand = FoodCommandFixture.UpdateByAdmin(
                foodId = food.id,
                adminId = 999L,
                name = food.name,
                perCapitaIntake = food.perCapitaIntake,
                unit = food.unit,
                carbohydrates = food.carbohydrates,
                protein = food.protein,
                fat = food.fat,
                isAdminApproved = true
            ).create()

            // when
            val updatedFood = foodDomainService.updateByAdmin(updateByAdminCommand)

            // then
            assertThat(updatedFood.isAdminApproved).isEqualTo(true)
        }
    }
}