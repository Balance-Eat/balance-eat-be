package org.balanceeat.api.food

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.food.FoodDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import java.time.LocalDateTime

@WebMvcTest(FoodV1Controller::class)
class FoodV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @MockkBean(relaxed = true)
    private lateinit var foodService: FoodService

    @Nested
    @DisplayName("POST /v1/foods - 음식 생성")
    inner class CreateTest {
        @Test
        fun success() {
            val request = mockCreateRequest()
            every { foodService.create(any(), any()) } returns mockFoodDto()

            given()
                .body(request)
                .post("/v1/foods")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("CreateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.FOOD.tagName)
                            .description(Tags.FOOD.descriptionWith("생성")),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.name" type STRING means "음식명",
                                "data.perCapitaIntake" type NUMBER means "1회 제공량",
                                "data.unit" type STRING means "단위",
                                "data.carbohydrates" type NUMBER means "탄수화물 (g)",
                                "data.protein" type NUMBER means "단백질 (g)",
                                "data.fat" type NUMBER means "지방 (g)",
                                "data.createdAt" type STRING means "생성일시",
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED);
        }

        private fun mockCreateRequest(): FoodV1Request.Create {
            return FoodV1Request.Create(
                uuid = "test-uuid-456",
                name = "새로운 음식",
                perCapitaIntake = 150.0,
                unit = "ml",
                carbohydrates = 30.0,
                protein = 10.0,
                fat = 5.0
            )
        }
    }

    @Nested
    @DisplayName("PUT /v1/foods/{id} - 음식 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            val request = mockUpdateRequest()
            every { foodService.update(any(), any(), any()) } returns mockFoodDto()

            given()
                .body(request)
                .put("/v1/foods/1")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.FOOD.tagName)
                            .description(Tags.FOOD.descriptionWith("수정")),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.name" type STRING means "음식명",
                                "data.perCapitaIntake" type NUMBER means "1회 제공량",
                                "data.unit" type STRING means "단위",
                                "data.carbohydrates" type NUMBER means "탄수화물 (g)",
                                "data.protein" type NUMBER means "단백질 (g)",
                                "data.fat" type NUMBER means "지방 (g)",
                                "data.createdAt" type STRING means "생성일시",
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }

        private fun mockUpdateRequest(): FoodV1Request.Update {
            return FoodV1Request.Update(
                name = "수정된 음식",
                perCapitaIntake = 200.0,
                unit = "g",
                carbohydrates = 40.0,
                protein = 15.0,
                fat = 8.0
            )
        }
    }

    @Nested
    @DisplayName("GET /v1/foods/{id} - 음식 상세 조회")
    inner class GetDetailsTest {
        @Test
        fun success() {
            every { foodService.getDetails(any()) } returns mockFoodDto()

            given()
                .get("/v1/foods/1")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetDetailsTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.FOOD.tagName)
                            .description(Tags.FOOD.descriptionWith("상세 조회")),
                        responseFields(
                            fieldsWithBasic(
                                "data.id" type NUMBER means "음식 ID",
                                "data.uuid" type STRING means "음식 UUID",
                                "data.name" type STRING means "음식명",
                                "data.perCapitaIntake" type NUMBER means "1회 제공량",
                                "data.unit" type STRING means "단위",
                                "data.carbohydrates" type NUMBER means "탄수화물 (g)",
                                "data.protein" type NUMBER means "단백질 (g)",
                                "data.fat" type NUMBER means "지방 (g)",
                                "data.createdAt" type STRING means "생성일시",
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("GET /v1/foods/recommendations - 추천 음식 조회")
    inner class GetRecommendationsTest {
        @Test
        fun success() {
            every { foodService.getRecommendations(any()) } returns listOf(mockFoodDto())

            given()
                .params("limit", 10)
                .get("/v1/foods/recommendations")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetRecommendationsTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.FOOD.tagName)
                            .description(Tags.FOOD.descriptionWith("목록 조회")),
                        responseFields(
                            fieldsWithBasic(
                                "data" type ARRAY means "추천 음식 목록",
                                "data[].id" type NUMBER means "음식 ID",
                                "data[].uuid" type STRING means "음식 UUID",
                                "data[].name" type STRING means "음식명",
                                "data[].perCapitaIntake" type NUMBER means "1회 제공량",
                                "data[].unit" type STRING means "단위",
                                "data[].carbohydrates" type NUMBER means "탄수화물 (g)",
                                "data[].protein" type NUMBER means "단백질 (g)",
                                "data[].fat" type NUMBER means "지방 (g)",
                                "data[].createdAt" type STRING means "생성일시",
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
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
}