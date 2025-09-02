package org.balanceeat.api.food

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.domain.food.FoodFixture
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FoodServiceTest: IntegrationTestContext() {
    @Autowired
    private lateinit var foodService: FoodService

    @Nested
    inner class UpdateTest {
        @Test
        fun `음식 생성자와 수정자와 동일하지 않을 경우 실패`() {
            // given
            val creatorId = 1L
            val modifierId = 2L
            val food = createEntity(FoodFixture(userId = creatorId).create())
            val request = FoodV1RequestFixture.Update().create()

            // when
            val throwable = catchThrowable {
                foodService.update(
                    request = request,
                    id = food.id,
                    modifierId = modifierId
                )
            }

            // then
            assertThat(throwable).isInstanceOf(BadRequestException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.CANNOT_MODIFY_FOOD)
        }
    }
}