package org.balanceeat.api.food

import com.fasterxml.jackson.databind.ObjectMapper
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.food.FoodDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willThrow
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import org.hamcrest.Matchers.aMapWithSize

@WebMvcTest(FoodV1Controller::class)
@DisplayName("FoodV1Controller 테스트")
class FoodV1ControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @MockitoBean
    private lateinit var foodService: FoodService

    @Nested
    @DisplayName("POST /v1/foods - 음식 생성")
    inner class CreateFoodTest {
        @Test
        fun `성공`() {
            // given
            val request = FoodV1Request.Create(
                uuid = "test-uuid-123",
                name = "테스트 음식",
                perCapitaIntake = 100.0,
                unit = "g",
                carbohydrates = 25.0,
                protein = 8.0,
                fat = 3.0
            )

            val mockFoodDto = mockFoodDto()
            given(foodService.create(request, 1L)).willReturn(mockFoodDto)

            // when & then
            mockMvc.perform(
                post("/v1/foods")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(mockFoodDto.id))
                .andExpect(jsonPath("$.data.uuid").value(mockFoodDto.uuid))
                .andExpect(jsonPath("$.data.name").value(mockFoodDto.name))
                .andExpect(jsonPath("$.data.perCapitaIntake").value(mockFoodDto.perCapitaIntake))
                .andExpect(jsonPath("$.data.unit").value(mockFoodDto.unit))
                .andExpect(jsonPath("$.data.carbohydrates").value(mockFoodDto.carbohydrates))
                .andExpect(jsonPath("$.data.protein").value(mockFoodDto.protein))
                .andExpect(jsonPath("$.data.fat").value(mockFoodDto.fat))
                .andExpect(jsonPath("$.data.isTest").value(false))
                .andExpect(jsonPath("$.data", aMapWithSize<Any, Any>(9)))
        }
    }

    @Nested
    @DisplayName("PUT /v1/foods/{id} - 음식 수정")
    inner class UpdateFoodTest {
        @Test
        fun `성공`() {
            // given
            val foodId = 1L
            val request = FoodV1Request.Update(
                name = "수정된 음식",
                perCapitaIntake = 150.0,
                unit = "g",
                carbohydrates = 30.0,
                protein = 12.0,
                fat = 5.0
            )

            val mockFoodDto = mockFoodDto()

            given(foodService.update(request, foodId, 1L)).willReturn(mockFoodDto)

            // when & then
            mockMvc.perform(
                put("/v1/foods/{id}", foodId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(mockFoodDto.id))
                .andExpect(jsonPath("$.data.uuid").value(mockFoodDto.uuid))
                .andExpect(jsonPath("$.data.name").value(mockFoodDto.name))
                .andExpect(jsonPath("$.data.perCapitaIntake").value(mockFoodDto.perCapitaIntake))
                .andExpect(jsonPath("$.data.unit").value(mockFoodDto.unit))
                .andExpect(jsonPath("$.data.carbohydrates").value(mockFoodDto.carbohydrates))
                .andExpect(jsonPath("$.data.protein").value(mockFoodDto.protein))
                .andExpect(jsonPath("$.data.fat").value(mockFoodDto.fat))
                .andExpect(jsonPath("$.data.isTest").value(false))
                .andExpect(jsonPath("$.data", aMapWithSize<Any, Any>(9)))
        }
    }

    @Nested
    @DisplayName("GET /v1/foods/{id} - 음식 상세 조회")
    inner class GetFoodDetailsTest {
        @Test
        fun `성공`() {
            // given
            val foodId = 1L
            val mockFoodDto = mockFoodDto()
            given(foodService.getDetails(foodId)).willReturn(mockFoodDto)

            // when & then
            mockMvc.perform(get("/v1/foods/{id}", foodId))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(foodId))
                .andExpect(jsonPath("$.data.uuid").value(mockFoodDto.uuid))
                .andExpect(jsonPath("$.data.name").value(mockFoodDto.name))
                .andExpect(jsonPath("$.data.perCapitaIntake").value(mockFoodDto.perCapitaIntake))
                .andExpect(jsonPath("$.data.unit").value(mockFoodDto.unit))
                .andExpect(jsonPath("$.data.carbohydrates").value(mockFoodDto.carbohydrates))
                .andExpect(jsonPath("$.data.protein").value(mockFoodDto.protein))
                .andExpect(jsonPath("$.data.fat").value(mockFoodDto.fat))
                .andExpect(jsonPath("$.data.isTest").value(false))
                .andExpect(jsonPath("$.data", aMapWithSize<Any, Any>(9)))
        }
    }

    private fun mockFoodDto(): FoodDto {
        return FoodDto(
            id = 1L,
            uuid = "test-uuid-123",
            name = "테스트 음식",
            userId = 1L,
            perCapitaIntake = 100.0,
            unit = "g",
            carbohydrates = 25.0,
            protein = 8.0,
            fat = 3.0,
            isAdminApproved = false,
            createdAt = LocalDateTime.now()
        )
    }

    @Nested
    @DisplayName("GET /v1/foods/recommendations - 추천 음식 조회")
    inner class GetRecommendationsTest {
        @Test
        fun `성공`() {
            // given
            val limit = 5
            val food1 = mockFoodDto()
            val food2 = food1.copy(id = 2L, uuid = "uuid-2", name = "두번째 음식")
            given(foodService.getRecommendations(limit)).willReturn(listOf(food1, food2))

            // when & then
            mockMvc.perform(get("/v1/foods/recommendations").param("limit", limit.toString()))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].id").value(food1.id))
                .andExpect(jsonPath("$.data[0].uuid").value(food1.uuid))
                .andExpect(jsonPath("$.data[0].name").value(food1.name))
                .andExpect(jsonPath("$.data[0].isTest").value(false))
                .andExpect(jsonPath("$.data[0]", aMapWithSize<Any, Any>(9)))
                .andExpect(jsonPath("$.data[1].id").value(food2.id))
                .andExpect(jsonPath("$.data[1].uuid").value(food2.uuid))
                .andExpect(jsonPath("$.data[1].name").value(food2.name))
                .andExpect(jsonPath("$.data[1].isTest").value(false))
                .andExpect(jsonPath("$.data[1]", aMapWithSize<Any, Any>(9)))
        }
    }
}