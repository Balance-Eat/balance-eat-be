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
import org.springframework.data.domain.PageRequest

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

    @Nested
    inner class SearchTest {
        @Test
        fun `음식 이름으로 필터링이 정상 동작한다`() {
            // given
            val userId = 900L
            val apple = createEntity(FoodFixture(name = "사과", userId = userId).create())
            val banana = createEntity(FoodFixture(name = "바나나", userId = userId).create())
            val appleJuice = createEntity(FoodFixture(name = "사과주스", userId = userId).create())

            val request = FoodV1RequestFixture.Search(foodName = "사과", userId = userId).create()
            val pageable = PageRequest.of(0, 10)

            // when
            val result = foodService.search(request, pageable)

            // then
            assertThat(result.items).hasSize(2)
            assertThat(result.items.map { it.name }).containsExactlyInAnyOrder("사과", "사과주스")
        }

        @Test
        fun `userId로 필터링이 정상 동작한다`() {
            // given
            val user1Id = 100L
            val user2Id = 200L
            val user1Food1 = createEntity(FoodFixture(name = "유저1 음식1", userId = user1Id).create())
            val user1Food2 = createEntity(FoodFixture(name = "유저1 음식2", userId = user1Id).create())
            val user2Food = createEntity(FoodFixture(name = "유저2 음식", userId = user2Id).create())

            val request = FoodV1RequestFixture.Search(userId = user1Id).create()
            val pageable = PageRequest.of(0, 10)

            // when
            val result = foodService.search(request, pageable)

            // then
            assertThat(result.items).hasSize(2)
            assertThat(result.items.map { it.userId }).allMatch { it == user1Id }
            assertThat(result.items.map { it.name }).containsExactlyInAnyOrder("유저1 음식1", "유저1 음식2")
        }

        @Test
        fun `동일한 조건일 때 최신 음식(높은 ID)이 먼저 나온다`() {
            // given
            val oldFood = createEntity(FoodFixture(name = "테스트 음식").create())
            Thread.sleep(10) // ID 차이를 보장하기 위한 짧은 대기
            val newFood = createEntity(FoodFixture(name = "테스트 음식").create())

            val request = FoodV1RequestFixture.Search(foodName = "테스트").create()
            val pageable = PageRequest.of(0, 10)

            // when
            val result = foodService.search(request, pageable)

            // then
            assertThat(result.items).hasSize(2)
            // 첫 번째 결과가 더 높은 ID(최신)여야 함
            assertThat(result.items[0].id).isGreaterThan(result.items[1].id)
            assertThat(result.items[0].id).isEqualTo(newFood.id)
            assertThat(result.items[1].id).isEqualTo(oldFood.id)
        }

        @Test
        fun `검색 조건에 맞는 결과가 없을 때 빈 페이지를 반환한다`() {
            // given
            val userId = 600L
            createEntity(FoodFixture(name = "사과", userId = userId).create())

            val request = FoodV1RequestFixture.Search(foodName = "존재하지않는음식", userId = userId).create()
            val pageable = PageRequest.of(0, 10)

            // when
            val result = foodService.search(request, pageable)

            // then
            assertThat(result.items).isEmpty()
            assertThat(result.totalItems).isEqualTo(0)
        }

        @Test
        fun `대소문자 구분 없이 음식 이름 검색이 동작한다`() {
            // given
            val userId = 800L
            val apple = createEntity(FoodFixture(name = "Apple", userId = userId).create())
            val banana = createEntity(FoodFixture(name = "BANANA", userId = userId).create())

            val request = FoodV1RequestFixture.Search(foodName = "apple", userId = userId).create()
            val pageable = PageRequest.of(0, 10)

            // when
            val result = foodService.search(request, pageable)

            // then
            assertThat(result.items).hasSize(1)
            assertThat(result.items[0].name).isEqualTo("Apple")
        }
    }
}
