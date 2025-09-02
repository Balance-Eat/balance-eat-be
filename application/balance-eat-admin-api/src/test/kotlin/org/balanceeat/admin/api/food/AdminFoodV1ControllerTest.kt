package org.balanceeat.admin.api.food

import com.fasterxml.jackson.databind.ObjectMapper
import org.balanceeat.domain.food.FoodDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(AdminFoodV1Controller::class)
@DisplayName("AdminFoodV1Controller 테스트")
class AdminFoodV1ControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @MockitoBean
    private lateinit var adminFoodService: AdminFoodService

    @Nested
    @DisplayName("PUT /admin/v1/foods/{id} - 어드민 음식 수정")
    inner class UpdateFoodTest {
        @Test
        fun `성공`() {
            // given
            val foodId = 1L
            val request = AdminFoodV1RequestFixture.Update().create()

            val mockFoodDto = mockFoodDto()
            given(adminFoodService.update(request, foodId, 1L)).willReturn(mockFoodDto)

            // when & then
            mockMvc.perform(
                put("/admin/v1/foods/{id}", foodId)
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
                .andExpect(jsonPath("$.data.isAdminApproved").value(mockFoodDto.isAdminApproved))
        }
    }

    @Nested
    @DisplayName("GET /admin/v1/foods/{id} - 어드민 음식 상세 조회")
    inner class GetFoodDetailsTest {
        @Test
        fun `성공`() {
            // given
            val foodId = 1L
            val mockFoodDto = mockFoodDto()
            given(adminFoodService.getDetails(foodId)).willReturn(mockFoodDto)

            // when & then
            mockMvc.perform(get("/admin/v1/foods/{id}", foodId))
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
                .andExpect(jsonPath("$.data.isAdminApproved").value(mockFoodDto.isAdminApproved))
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
            isAdminApproved = true,
            createdAt = LocalDateTime.now()
        )
    }
}