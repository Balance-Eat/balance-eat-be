package org.balanceeat.api.food

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.domain.curation.CurationFoodFixture
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

    @Nested
    inner class GetRecommendationsTest {
        @Test
        fun `추천 음식은 curation weight 내림차순으로 정렬되고 limit 만큼 반환`() {
            // given
            val food1 = createEntity(FoodFixture(name = "닭가슴살").create())
            val food2 = createEntity(FoodFixture(name = "고구마").create())
            val food3 = createEntity(FoodFixture(name = "바나나").create())

            // curation weights: food3 > food1 > food2
            createEntity(CurationFoodFixture(foodId = food1.id, weight = 150).create())
            createEntity(CurationFoodFixture(foodId = food2.id, weight = 120).create())
            createEntity(CurationFoodFixture(foodId = food3.id, weight = 200).create())

            // when
            val result = foodService.getRecommendations(limit = 2)

            // then
            assertThat(result).hasSize(2)
            // order: food3(200), food1(150)
            assertThat(result.map { it.id }).containsExactly(food3.id, food1.id)
        }
    }
}
