package org.balanceeat.api.user

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.user.User
import org.balanceeat.domain.user.UserDto
import org.balanceeat.domain.user.UserDomainService
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
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import java.time.LocalDateTime

@WebMvcTest(UserV1Controller::class)
class UserV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean(relaxed = true)
    private lateinit var userDomainService: UserDomainService

    @Nested
    @DisplayName("POST /v1/users - 사용자 생성")
    inner class CreateTest {
        @Test
        fun success() {
            val request = mockCreateRequest()
            justRun { userDomainService.create(any()) }

            given()
                .body(request)
                .post("/v1/users")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("CreateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.USER.tagName)
                            .description(Tags.USER.descriptionWith("생성")),
                        requestFields(
                            "uuid" type STRING means "사용자 UUID",
                            "name" type STRING means "사용자 이름",
                            "gender" type STRING means "사용자 성별" withEnum User.ActivityLevel::class,
                            "age" type NUMBER means "사용자 나이",
                            "height" type NUMBER means "사용자 키 (cm 단위)",
                            "weight" type NUMBER means "사용자 몸무게 (kg 단위)",
                            "email" type STRING means "사용자 이메일 (선택)" isOptional true,
                            "activityLevel" type STRING means "사용자 활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true,
                            "smi" type NUMBER means "사용자 SMI (선택)" isOptional true,
                            "fatPercentage" type NUMBER means "사용자 체지방률 (선택)" isOptional true,
                            "targetWeight" type NUMBER means "사용자 목표 체중 (선택)" isOptional true,
                            "targetCalorie" type NUMBER means "사용자 목표 칼로리 (선택)" isOptional true,
                            "targetSmi" type NUMBER means "사용자 목표 SMI (선택)" isOptional true,
                            "targetFatPercentage" type NUMBER means "사용자 목표 체지방률 (선택)" isOptional true,
                            "targetCarbohydrates" type NUMBER means "사용자 목표 탄수화물 (g, 선택)" isOptional true,
                            "targetProtein" type NUMBER means "사용자 목표 단백질 (g, 선택)" isOptional true,
                            "targetFat" type NUMBER means "사용자 목표 지방 (g, 선택)" isOptional true,
                            "providerId" type STRING means "제공자 ID (선택)" isOptional true,
                            "providerType" type STRING means "제공자 유형 (선택)" isOptional true
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data" type NULL means "생성 결과 (null)"
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED)
        }

        private fun mockCreateRequest(): UserV1Request.Create {
            return UserV1Request.Create(
                uuid = "test-uuid-456",
                name = "새로운 사용자",
                gender = User.Gender.FEMALE,
                age = 28,
                height = 160.0,
                weight = 50.0,
                email = "new@example.com",
                activityLevel = User.ActivityLevel.MODERATE,
                smi = 18.5,
                fatPercentage = 22.0,
                targetWeight = 48.0,
                targetCalorie = 1600,
                targetSmi = 19.0,
                targetFatPercentage = 20.0,
                targetCarbohydrates = 200.0,
                targetProtein = 80.0,
                targetFat = 50.0,
                providerId = "kakao123",
                providerType = "KAKAO"
            )
        }
    }

    @Nested
    @DisplayName("GET /v1/users/me - 사용자 정보 조회")
    inner class GetMeTest {
        @Test
        fun success() {
            every { userDomainService.findByUuid(any()) } returns mockUser()

            given()
                .params("uuid", "test-uuid-123")
                .get("/v1/users/me")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetMeTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.USER.tagName)
                            .description(Tags.USER.descriptionWith("정보 조회")),
                        queryParameters(
                            "uuid" queryMeans "사용자 UUID" isOptional true
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "사용자 ID",
                                "data.uuid" type STRING means "사용자 UUID",
                                "data.name" type STRING means "사용자 이름",
                                "data.email" type STRING means "사용자 이메일",
                                "data.gender" type STRING means "사용자 성별" withEnum User.Gender::class,
                                "data.age" type NUMBER means "사용자 나이",
                                "data.weight" type NUMBER means "사용자 몸무게",
                                "data.height" type NUMBER means "사용자 키",
                                "data.activityLevel" type STRING means "사용자 활동 수준" withEnum User.ActivityLevel::class,
                                "data.smi" type NUMBER means "사용자 SMI",
                                "data.fatPercentage" type NUMBER means "사용자 체지방률",
                                "data.targetWeight" type NUMBER means "사용자 목표 체중",
                                "data.targetCalorie" type NUMBER means "사용자 목표 칼로리",
                                "data.targetSmi" type NUMBER means "사용자 목표 SMI",
                                "data.targetFatPercentage" type NUMBER means "사용자 목표 체지방률",
                                "data.targetCarbohydrates" type NUMBER means "사용자 목표 탄수화물",
                                "data.targetProtein" type NUMBER means "사용자 목표 단백질",
                                "data.targetFat" type NUMBER means "사용자 목표 지방",
                                "data.providerId" type STRING means "제공자 ID",
                                "data.providerType" type STRING means "제공자 유형"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("PUT /v1/users/{id} - 사용자 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            val request = mockUpdateRequest()
            justRun { userDomainService.update(any(), any()) }

            given()
                .body(request)
                .put("/v1/users/{id}", 1)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.USER.tagName)
                            .description(Tags.USER.descriptionWith("수정")),
                        pathParameters(
                            "id" pathMeans "사용자 ID"
                        ),
                        requestFields(
                            "name" type STRING means "사용자 이름 (선택)" isOptional true,
                            "email" type STRING means "사용자 이메일 (선택)" isOptional true,
                            "gender" type STRING means "사용자 성별 (선택)" withEnum User.Gender::class isOptional true,
                            "age" type NUMBER means "사용자 나이 (선택)" isOptional true,
                            "height" type NUMBER means "사용자 키 (cm 단위) (선택)" isOptional true,
                            "weight" type NUMBER means "사용자 몸무게 (kg 단위) (선택)" isOptional true,
                            "activityLevel" type STRING means "사용자 활동 수준 (선택)" withEnum User.ActivityLevel::class isOptional true,
                            "smi" type NUMBER means "사용자 SMI (선택)" isOptional true,
                            "fatPercentage" type NUMBER means "사용자 체지방률 (선택)" isOptional true,
                            "targetWeight" type NUMBER means "사용자 목표 체중 (선택)" isOptional true,
                            "targetCalorie" type NUMBER means "사용자 목표 칼로리 (선택)" isOptional true,
                            "targetSmi" type NUMBER means "사용자 목표 SMI (선택)" isOptional true,
                            "targetFatPercentage" type NUMBER means "사용자 목표 체지방률 (선택)" isOptional true,
                            "targetCarbohydrates" type NUMBER means "사용자 목표 탄수화물 (g, 선택)" isOptional true,
                            "targetProtein" type NUMBER means "사용자 목표 단백질 (g, 선택)" isOptional true,
                            "targetFat" type NUMBER means "사용자 목표 지방 (g, 선택)" isOptional true
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data" type NULL means "수정 결과 (null)"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }

        private fun mockUpdateRequest(): UserV1Request.Update {
            return UserV1Request.Update(
                name = "수정된 사용자",
                email = "updated@example.com",
                gender = User.Gender.MALE,
                age = 30,
                height = 175.0,
                weight = 70.0,
                activityLevel = User.ActivityLevel.ACTIVE,
                smi = 22.0,
                fatPercentage = 15.0,
                targetWeight = 68.0,
                targetCalorie = 2200,
                targetSmi = 23.0,
                targetFatPercentage = 12.0,
                targetCarbohydrates = 300.0,
                targetProtein = 120.0,
                targetFat = 80.0
            )
        }
    }

    private fun mockUser(): User {
        return User(
            id = 1L,
            uuid = "test-uuid-123",
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
            providerType = "KAKAO"
        )
    }

    private fun mockUserDto(): UserDto {
        return UserDto(
            id = 1L,
            uuid = "test-uuid-123",
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