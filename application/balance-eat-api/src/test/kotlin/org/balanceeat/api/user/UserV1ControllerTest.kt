package org.balanceeat.api.user

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.user.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters

@WebMvcTest(UserV1Controller::class)
class UserV1ControllerTest: ControllerTestContext() {
    @MockkBean(relaxed = true)
    private lateinit var userService: UserService

    @Nested
    @DisplayName("POST /v1/users - 사용자 생성")
    inner class CreateTest {
        @Test
        fun success() {
            val request = mockCreateRequest()
            every { userService.create(any()) } returns mockUser()

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
                            "goalType" type STRING means "사용자 목표 타입" withEnum User.GoalType::class,
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
                                "data.id" type NUMBER means "사용자 ID",
                                "data.uuid" type STRING means "사용자 UUID",
                                "data.name" type STRING means "사용자 이름",
                                "data.email" type STRING means "사용자 이메일" isOptional true,
                                "data.gender" type STRING means "사용자 성별" withEnum User.Gender::class,
                                "data.age" type NUMBER means "사용자 나이",
                                "data.weight" type NUMBER means "사용자 몸무게",
                                "data.height" type NUMBER means "사용자 키",
                                "data.goalType" type STRING means "사용자 목표 타입" withEnum User.GoalType::class,
                                "data.activityLevel" type STRING means "사용자 활동 수준" withEnum User.ActivityLevel::class isOptional true,
                                "data.smi" type NUMBER means "사용자 SMI" isOptional true,
                                "data.fatPercentage" type NUMBER means "사용자 체지방률" isOptional true,
                                "data.targetWeight" type NUMBER means "사용자 목표 체중" isOptional true,
                                "data.targetCalorie" type NUMBER means "사용자 목표 칼로리" isOptional true,
                                "data.targetSmi" type NUMBER means "사용자 목표 SMI" isOptional true,
                                "data.targetFatPercentage" type NUMBER means "사용자 목표 체지방률" isOptional true,
                                "data.targetCarbohydrates" type NUMBER means "사용자 목표 탄수화물" isOptional true,
                                "data.targetProtein" type NUMBER means "사용자 목표 단백질" isOptional true,
                                "data.targetFat" type NUMBER means "사용자 목표 지방" isOptional true,
                                "data.providerId" type STRING means "제공자 ID" isOptional true,
                                "data.providerType" type STRING means "제공자 유형" isOptional true
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
                goalType = User.GoalType.DIET,
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
            every { userService.findByUuid(any()) } returns mockUser()

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
                                "data.email" type STRING means "사용자 이메일" isOptional true,
                                "data.gender" type STRING means "사용자 성별" withEnum User.Gender::class,
                                "data.age" type NUMBER means "사용자 나이",
                                "data.weight" type NUMBER means "사용자 몸무게",
                                "data.height" type NUMBER means "사용자 키",
                                "data.goalType" type STRING means "사용자 목표 타입" withEnum User.GoalType::class,
                                "data.activityLevel" type STRING means "사용자 활동 수준" withEnum User.ActivityLevel::class isOptional true,
                                "data.smi" type NUMBER means "사용자 SMI" isOptional true,
                                "data.fatPercentage" type NUMBER means "사용자 체지방률" isOptional true,
                                "data.targetWeight" type NUMBER means "사용자 목표 체중" isOptional true,
                                "data.targetCalorie" type NUMBER means "사용자 목표 칼로리" isOptional true,
                                "data.targetSmi" type NUMBER means "사용자 목표 SMI" isOptional true,
                                "data.targetFatPercentage" type NUMBER means "사용자 목표 체지방률" isOptional true,
                                "data.targetCarbohydrates" type NUMBER means "사용자 목표 탄수화물" isOptional true,
                                "data.targetProtein" type NUMBER means "사용자 목표 단백질" isOptional true,
                                "data.targetFat" type NUMBER means "사용자 목표 지방" isOptional true,
                                "data.providerId" type STRING means "제공자 ID" isOptional true,
                                "data.providerType" type STRING means "제공자 유형" isOptional true
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
            every { userService.update(any(), any()) } returns mockUser()

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
                            "name" type STRING means "사용자 이름",
                            "gender" type STRING means "사용자 성별" withEnum User.Gender::class,
                            "age" type NUMBER means "사용자 나이",
                            "height" type NUMBER means "사용자 키 (cm 단위)",
                            "weight" type NUMBER means "사용자 몸무게 (kg 단위)",
                            "goalType" type STRING means "사용자 목표 타입" withEnum User.GoalType::class,
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
                            "targetFat" type NUMBER means "사용자 목표 지방 (g, 선택)" isOptional true
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "사용자 ID",
                                "data.uuid" type STRING means "사용자 UUID",
                                "data.name" type STRING means "사용자 이름",
                                "data.email" type STRING means "사용자 이메일" isOptional true,
                                "data.gender" type STRING means "사용자 성별" withEnum User.Gender::class,
                                "data.age" type NUMBER means "사용자 나이",
                                "data.weight" type NUMBER means "사용자 몸무게",
                                "data.height" type NUMBER means "사용자 키",
                                "data.goalType" type STRING means "사용자 목표 타입" withEnum User.GoalType::class,
                                "data.activityLevel" type STRING means "사용자 활동 수준" withEnum User.ActivityLevel::class isOptional true,
                                "data.smi" type NUMBER means "사용자 SMI" isOptional true,
                                "data.fatPercentage" type NUMBER means "사용자 체지방률" isOptional true,
                                "data.targetWeight" type NUMBER means "사용자 목표 체중" isOptional true,
                                "data.targetCalorie" type NUMBER means "사용자 목표 칼로리" isOptional true,
                                "data.targetSmi" type NUMBER means "사용자 목표 SMI" isOptional true,
                                "data.targetFatPercentage" type NUMBER means "사용자 목표 체지방률" isOptional true,
                                "data.targetCarbohydrates" type NUMBER means "사용자 목표 탄수화물" isOptional true,
                                "data.targetProtein" type NUMBER means "사용자 목표 단백질" isOptional true,
                                "data.targetFat" type NUMBER means "사용자 목표 지방" isOptional true,
                                "data.providerId" type STRING means "제공자 ID" isOptional true,
                                "data.providerType" type STRING means "제공자 유형" isOptional true
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }

        private fun mockUpdateRequest(): UserV1Request.Update {
            return UserV1RequestFixture.Update()
                .create()
        }
    }

    private fun mockUser(): UserV1Response.Details {
        return UserV1ResponseFixture.Details().create()
    }
}