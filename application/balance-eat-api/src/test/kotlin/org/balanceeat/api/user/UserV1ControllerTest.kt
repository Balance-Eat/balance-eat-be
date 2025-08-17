package org.balanceeat.api.user

import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.balanceeat.domain.user.UserCommand
import org.balanceeat.domain.user.UserDomainService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserV1Controller::class)
class UserV1ControllerTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun userV1Controller(userDomainService: UserDomainService): UserV1Controller {
            return UserV1Controller(userDomainService)
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userDomainService: UserDomainService

    @Nested
    @DisplayName("사용자 생성")
    inner class CreateUserTest {

        @Test
        fun `사용자_생성_성공`() {
            // given
            every { userDomainService.create(any<UserCommand.Create>()) } just runs

            val request = """
                {
                    "uuid": "test-uuid-123",
                    "name": "테스트유저",
                    "gender": "MALE",
                    "age": 25,
                    "height": 170.0,
                    "weight": 70.0,
                    "email": "test@example.com",
                    "activityLevel": "MODERATE"
                }
            """.trimIndent()

            // when & then
            mockMvc.perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.status").value("SUCCESS"))
        }

        @Test
        fun `필수_필드_누락시_400_에러`() {
            // given
            val request = """
                {
                    "name": "테스트유저"
                }
            """.trimIndent()

            // when & then
            mockMvc.perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
                .andExpect(status().isBadRequest)
        }
    }
}