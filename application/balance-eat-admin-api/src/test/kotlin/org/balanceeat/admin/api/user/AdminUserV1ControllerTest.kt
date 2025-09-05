package org.balanceeat.admin.api.user

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.admin.api.supports.ControllerTestContext
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import java.time.LocalDateTime

@WebMvcTest(AdminUserV1Controller::class)
class AdminUserV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean(relaxed = true)
    private lateinit var adminUserService: AdminUserService

    @Nested
    @DisplayName("PUT /admin/v1/users/{id} - 어드민 유저 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            val request = mockUpdateRequest()
            every { adminUserService.update(any(), any(), any()) } returns mockUserDto()

            given()
                .body(request)
                .put("/admin/v1/users/{id}", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.ADMIN_USER.tagName)
                            .description(Tags.ADMIN_USER.descriptionWith("수정")),
                        pathParameters(
                            "id" pathMeans "유저 ID"
                        ),
                        requestFields(
                            "name" type STRING means "이름 (선택)" isOptional true,
                            "email" type STRING means "이메일 (선택)" isOptional true,
                            "gender" type STRING means "성별 (선택)" withEnum User.Gender::class isOptional true,
                            "age" type NUMBER means "나이 (선택)" isOptional true,
                            "height" type NUMBER means "키 (cm) (선택)" isOptional true,
                            "weight" type NUMBER means "몸무게 (kg) (선택)" isOptional true,
                            "activityLevel" type STRING means "활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true,
                            "smi" type NUMBER means "SMI (선택)" isOptional true,
                            "fatPercentage" type NUMBER means "체지방률 (%) (선택)" isOptional true,
                            "targetWeight" type NUMBER means "목표 몸무게 (kg) (선택)" isOptional true,
                            "targetCalorie" type NUMBER means "목표 칼로리 (kcal) (선택)" isOptional true,
                            "targetSmi" type NUMBER means "목표 SMI (선택)" isOptional true,
                            "targetFatPercentage" type NUMBER means "목표 체지방률 (%) (선택)" isOptional true,
                            "targetCarbohydrates" type NUMBER means "목표 탄수화물 (g) (선택)" isOptional true,
                            "targetProtein" type NUMBER means "목표 단백질 (g) (선택)" isOptional true,
                            "targetFat" type NUMBER means "목표 지방 (g) (선택)" isOptional true,
                            "providerId" type STRING means "Provider ID (선택)" isOptional true,
                            "providerType" type STRING means "Provider Type (선택)" isOptional true
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "유저 ID",
                                "data.uuid" type STRING means "유저 UUID",
                                "data.name" type STRING means "이름",
                                "data.email" type STRING means "이메일",
                                "data.gender" type STRING means "성별" withEnum User.Gender::class,
                                "data.age" type NUMBER means "나이",
                                "data.height" type NUMBER means "키 (cm)",
                                "data.weight" type NUMBER means "몸무게 (kg)",
                                "data.activityLevel" type STRING means "활동 수준" withEnum User.ActivityLevel::class,
                                "data.smi" type NUMBER means "SMI",
                                "data.fatPercentage" type NUMBER means "체지방률 (%)",
                                "data.targetWeight" type NUMBER means "목표 몸무게 (kg)",
                                "data.targetCalorie" type NUMBER means "목표 칼로리 (kcal)",
                                "data.targetSmi" type NUMBER means "목표 SMI",
                                "data.targetFatPercentage" type NUMBER means "목표 체지방률 (%)",
                                "data.targetCarbohydrates" type NUMBER means "목표 탄수화물 (g)",
                                "data.targetProtein" type NUMBER means "목표 단백질 (g)",
                                "data.targetFat" type NUMBER means "목표 지방 (g)",
                                "data.providerId" type STRING means "Provider ID",
                                "data.providerType" type STRING means "Provider Type"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }

        private fun mockUpdateRequest(): AdminUserV1Request.Update {
            return AdminUserV1Request.Update(
                name = "수정된 사용자",
                email = "updated@example.com",
                gender = User.Gender.MALE,
                age = 30,
                height = 175.0,
                weight = 70.0,
                activityLevel = User.ActivityLevel.MODERATE,
                smi = 22.0,
                fatPercentage = 15.0,
                targetWeight = 68.0,
                targetCalorie = 2000,
                targetSmi = 23.0,
                targetFatPercentage = 12.0,
                targetCarbohydrates = 300.0,
                targetProtein = 120.0,
                targetFat = 70.0,
                providerId = "kakao789",
                providerType = "KAKAO"
            )
        }
    }

    @Nested
    @DisplayName("GET /admin/v1/users/{id} - 어드민 유저 상세 조회")
    inner class GetDetailsTest {
        @Test
        fun success() {
            every { adminUserService.getDetails(any()) } returns mockUserDto()

            given()
                .get("/admin/v1/users/{id}", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetDetailsTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.ADMIN_USER.tagName)
                            .description(Tags.ADMIN_USER.descriptionWith("상세 조회")),
                        pathParameters(
                            "id" pathMeans "유저 ID"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "유저 ID",
                                "data.uuid" type STRING means "유저 UUID",
                                "data.name" type STRING means "이름",
                                "data.email" type STRING means "이메일",
                                "data.gender" type STRING means "성별" withEnum User.Gender::class,
                                "data.age" type NUMBER means "나이",
                                "data.height" type NUMBER means "키 (cm)",
                                "data.weight" type NUMBER means "몸무게 (kg)",
                                "data.activityLevel" type STRING means "활동 수준" withEnum User.ActivityLevel::class,
                                "data.smi" type NUMBER means "SMI",
                                "data.fatPercentage" type NUMBER means "체지방률 (%)",
                                "data.targetWeight" type NUMBER means "목표 몸무게 (kg)",
                                "data.targetCalorie" type NUMBER means "목표 칼로리 (kcal)",
                                "data.targetSmi" type NUMBER means "목표 SMI",
                                "data.targetFatPercentage" type NUMBER means "목표 체지방률 (%)",
                                "data.targetCarbohydrates" type NUMBER means "목표 탄수화물 (g)",
                                "data.targetProtein" type NUMBER means "목표 단백질 (g)",
                                "data.targetFat" type NUMBER means "목표 지방 (g)",
                                "data.providerId" type STRING means "Provider ID",
                                "data.providerType" type STRING means "Provider Type"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
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