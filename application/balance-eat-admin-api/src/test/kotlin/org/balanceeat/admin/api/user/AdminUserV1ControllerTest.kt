package org.balanceeat.admin.api.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserDto
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

@WebMvcTest(AdminUserV1Controller::class)
@DisplayName("AdminUserV1Controller 테스트")
class AdminUserV1ControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @MockitoBean
    private lateinit var adminUserService: AdminUserService

    @Nested
    @DisplayName("PUT /admin/v1/users/{id} - 어드민 유저 수정")
    inner class UpdateUserTest {
        @Test
        fun `성공`() {
            // given
            val userId = 1L
            val request = AdminUserV1RequestFixture.Update().create()

            val mockUserDto = mockUserDto()
            given(adminUserService.update(request, userId, 1L)).willReturn(mockUserDto)

            // when & then
            mockMvc.perform(
                put("/admin/v1/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(mockUserDto.id))
                .andExpect(jsonPath("$.data.uuid").value(mockUserDto.uuid))
                .andExpect(jsonPath("$.data.name").value(mockUserDto.name))
                .andExpect(jsonPath("$.data.email").value(mockUserDto.email))
                .andExpect(jsonPath("$.data.gender").value(mockUserDto.gender.name))
                .andExpect(jsonPath("$.data.age").value(mockUserDto.age))
                .andExpect(jsonPath("$.data.height").value(mockUserDto.height))
                .andExpect(jsonPath("$.data.weight").value(mockUserDto.weight))
                .andExpect(jsonPath("$.data.activityLevel").value(mockUserDto.activityLevel?.name))
                .andExpect(jsonPath("$.data.smi").value(mockUserDto.smi))
                .andExpect(jsonPath("$.data.fatPercentage").value(mockUserDto.fatPercentage))
                .andExpect(jsonPath("$.data.targetWeight").value(mockUserDto.targetWeight))
                .andExpect(jsonPath("$.data.targetCalorie").value(mockUserDto.targetCalorie))
                .andExpect(jsonPath("$.data.targetSmi").value(mockUserDto.targetSmi))
                .andExpect(jsonPath("$.data.targetFatPercentage").value(mockUserDto.targetFatPercentage))
                .andExpect(jsonPath("$.data.targetCarbohydrates").value(mockUserDto.targetCarbohydrates))
                .andExpect(jsonPath("$.data.targetProtein").value(mockUserDto.targetProtein))
                .andExpect(jsonPath("$.data.targetFat").value(mockUserDto.targetFat))
                .andExpect(jsonPath("$.data.providerId").value(mockUserDto.providerId))
                .andExpect(jsonPath("$.data.providerType").value(mockUserDto.providerType))
        }

        @Test
        fun `부분 수정 성공 - 이름과 이메일만 수정`() {
            // given
            val userId = 1L
            val request = AdminUserV1RequestFixture.Update().apply {
                name = "부분 수정 사용자"
                email = "partial@example.com"
                gender = null
                age = null
                height = null
                weight = null
                activityLevel = null
                smi = null
                fatPercentage = null
                targetWeight = null
                targetCalorie = null
                targetSmi = null
                targetFatPercentage = null
                targetCarbohydrates = null
                targetProtein = null
                targetFat = null
                providerId = null
                providerType = null
            }.create()

            val mockUserDto = mockUserDto().copy(
                name = "부분 수정 사용자",
                email = "partial@example.com"
            )
            given(adminUserService.update(request, userId, 1L)).willReturn(mockUserDto)

            // when & then
            mockMvc.perform(
                put("/admin/v1/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("부분 수정 사용자"))
                .andExpect(jsonPath("$.data.email").value("partial@example.com"))
        }

        @Test
        fun `유효하지 않은 이메일 형식으로 실패`() {
            // given
            val userId = 1L
            val request = AdminUserV1RequestFixture.Update().apply {
                email = "invalid-email"
            }.create()

            // when & then
            mockMvc.perform(
                put("/admin/v1/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `나이 범위 초과로 실패`() {
            // given
            val userId = 1L
            val request = AdminUserV1RequestFixture.Update().apply {
                age = 200
            }.create()

            // when & then
            mockMvc.perform(
                put("/admin/v1/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("GET /admin/v1/users/{id} - 어드민 유저 상세 조회")
    inner class GetUserDetailsTest {
        @Test
        fun `성공`() {
            // given
            val userId = 1L
            val mockUserDto = mockUserDto()
            given(adminUserService.getDetails(userId)).willReturn(mockUserDto)

            // when & then
            mockMvc.perform(get("/admin/v1/users/{id}", userId))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.uuid").value(mockUserDto.uuid))
                .andExpect(jsonPath("$.data.name").value(mockUserDto.name))
                .andExpect(jsonPath("$.data.email").value(mockUserDto.email))
                .andExpect(jsonPath("$.data.gender").value(mockUserDto.gender.name))
                .andExpect(jsonPath("$.data.age").value(mockUserDto.age))
                .andExpect(jsonPath("$.data.height").value(mockUserDto.height))
                .andExpect(jsonPath("$.data.weight").value(mockUserDto.weight))
                .andExpect(jsonPath("$.data.activityLevel").value(mockUserDto.activityLevel?.name))
                .andExpect(jsonPath("$.data.smi").value(mockUserDto.smi))
                .andExpect(jsonPath("$.data.fatPercentage").value(mockUserDto.fatPercentage))
                .andExpect(jsonPath("$.data.targetWeight").value(mockUserDto.targetWeight))
                .andExpect(jsonPath("$.data.targetCalorie").value(mockUserDto.targetCalorie))
                .andExpect(jsonPath("$.data.targetSmi").value(mockUserDto.targetSmi))
                .andExpect(jsonPath("$.data.targetFatPercentage").value(mockUserDto.targetFatPercentage))
                .andExpect(jsonPath("$.data.targetCarbohydrates").value(mockUserDto.targetCarbohydrates))
                .andExpect(jsonPath("$.data.targetProtein").value(mockUserDto.targetProtein))
                .andExpect(jsonPath("$.data.targetFat").value(mockUserDto.targetFat))
                .andExpect(jsonPath("$.data.providerId").value(mockUserDto.providerId))
                .andExpect(jsonPath("$.data.providerType").value(mockUserDto.providerType))
        }
    }

    private fun mockUserDto(): UserDto {
        return UserDto(
            id = 1L,
            uuid = "test-user-uuid-123",
            name = "테스트 사용자",
            email = "test@example.com",
            gender = User.Gender.FEMALE,
            age = 25,
            weight = 55.0,
            height = 165.0,
            activityLevel = User.ActivityLevel.ACTIVE,
            smi = 20.0,
            fatPercentage = 18.0,
            targetWeight = 52.0,
            targetCalorie = 1800,
            targetSmi = 21.0,
            targetFatPercentage = 16.0,
            targetCarbohydrates = 250.0,
            targetProtein = 100.0,
            targetFat = 60.0,
            providerId = "kakao456",
            providerType = "KAKAO",
            createdAt = LocalDateTime.now()
        )
    }
}