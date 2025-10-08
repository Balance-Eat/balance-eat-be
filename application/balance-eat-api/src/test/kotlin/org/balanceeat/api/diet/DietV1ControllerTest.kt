package org.balanceeat.api.diet

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.NutritionInfo
import org.balanceeat.jackson.JsonUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(DietV1Controller::class)
class DietV1ControllerTest: ControllerTestContext() {
    @MockkBean(relaxed = true)
    private lateinit var dietService: DietService

    @Nested
    @DisplayName("GET /v1/diets/daily - 일별 식단 조회")
    inner class GetDietsByDateTest {
        @Test
        fun success() {
            // given
            every { dietService.getDailyDiets(any(), any()) } returns mockSummaryResponse()

            given()
                .header("X-USER-ID", "1")
                .params("date", "2025-08-04")
                .get("/v1/diets/daily")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetDietsByDateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.DIET.tagName)
                            .description(Tags.DIET.descriptionWith("일별 식단 조회")),
                        queryParameters(
                            "date" queryMeans "조회할 날짜 (yyyy-MM-dd)"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data" type ARRAY means "식사 기록 목록",
                                "data[].dietId" type NUMBER means "식사 기록 고유 ID",
                                "data[].mealType" type STRING means "식사 유형" withEnum Diet.MealType::class,
                                "data[].consumeDate" type STRING means "섭취 날짜 (yyyy-MM-dd)",
                                "data[].consumedAt" type STRING means "섭취 시간",
                                "data[].items" type ARRAY means "해당 식사에 포함된 음식 목록",
                                "data[].items[].foodId" type NUMBER means "음식 ID",
                                "data[].items[].foodName" type STRING means "음식 이름",
                                "data[].items[].intake" type NUMBER means "섭취량",
                                "data[].items[].unit" type STRING means "섭취량 단위",
                                "data[].items[].calories" type NUMBER means "해당 섭취량의 칼로리 (계산된 값)",
                                "data[].items[].carbohydrates" type NUMBER means "해당 섭취량의 탄수화물 (계산된 값)",
                                "data[].items[].protein" type NUMBER means "해당 섭취량의 단백질 (계산된 값)",
                                "data[].items[].fat" type NUMBER means "해당 섭취량의 지방 (계산된 값)"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("GET /v1/diets/monthly - 월별 식단 조회")
    inner class GetDietsByMonthTest {
        @Test
        fun success() {
            // given
            every { dietService.getMonthlyDiets(any(), any()) } returns mockSummaryResponse()

            given()
                .header("X-USER-ID", "1")
                .params("yearMonth", "2024-03")
                .get("/v1/diets/monthly")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetDietsByMonthTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.DIET.tagName)
                            .description(Tags.DIET.descriptionWith("월별 식단 조회")),
                        queryParameters(
                            "yearMonth" queryMeans "조회할 연월 (yyyy-MM)"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data" type ARRAY means "식사 기록 목록",
                                "data[].dietId" type NUMBER means "식사 기록 고유 ID",
                                "data[].mealType" type STRING means "식사 유형" withEnum Diet.MealType::class,
                                "data[].consumeDate" type STRING means "섭취 날짜 (yyyy-MM-dd)",
                                "data[].consumedAt" type STRING means "섭취 시간",
                                "data[].items" type ARRAY means "해당 식사에 포함된 음식 목록",
                                "data[].items[].foodId" type NUMBER means "음식 ID",
                                "data[].items[].foodName" type STRING means "음식 이름",
                                "data[].items[].intake" type NUMBER means "섭취량",
                                "data[].items[].unit" type STRING means "섭취량 단위",
                                "data[].items[].calories" type NUMBER means "해당 섭취량의 칼로리 (계산된 값)",
                                "data[].items[].carbohydrates" type NUMBER means "해당 섭취량의 탄수화물 (계산된 값)",
                                "data[].items[].protein" type NUMBER means "해당 섭취량의 단백질 (계산된 값)",
                                "data[].items[].fat" type NUMBER means "해당 섭취량의 지방 (계산된 값)"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    @Nested
    @DisplayName("POST /v1/diets - 식단 생성")
    inner class CreateTest {
        @Test
        fun success() {
            // given
            val request = mockCreateDietRequest()
            every { dietService.create(any(), any()) } returns mockDetailsResponse()
            
            given()
                .header("X-USER-ID", "1")
                .body(JsonUtils.stringify(request))
                .post("/v1/diets")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("CreateDietTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.DIET.tagName)
                            .description(Tags.DIET.descriptionWith("생성")),
                        requestFields(
                            "mealType" type STRING means "식사 유형" withEnum Diet.MealType::class,
                            "consumedAt" type STRING means "섭취 시간",
                            "dietFoods" type ARRAY means "섭취한 음식 목록",
                            "dietFoods[].foodId" type NUMBER means "음식 ID",
                            "dietFoods[].intake" type NUMBER means "섭취량"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.dietId" type NUMBER means "생성된 식단 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.mealType" type STRING means "식사 유형" withEnum Diet.MealType::class,
                                "data.consumeDate" type STRING means "섭취 날짜 (yyyy-MM-dd)",
                                "data.consumedAt" type STRING means "섭취 시간",
                                "data.totalNutrition" type OBJECT means "총 영양성분",
                                "data.totalNutrition.calories" type NUMBER means "총 칼로리 (kcal)",
                                "data.totalNutrition.carbohydrates" type NUMBER means "총 탄수화물 (g)",
                                "data.totalNutrition.protein" type NUMBER means "총 단백질 (g)",
                                "data.totalNutrition.fat" type NUMBER means "총 지방 (g)",
                                "data.dietFoods" type ARRAY means "섭취한 음식 목록",
                                "data.dietFoods[].id" type NUMBER means "식단 음식 ID",
                                "data.dietFoods[].foodId" type NUMBER means "음식 ID",
                                "data.dietFoods[].foodName" type STRING means "음식 이름",
                                "data.dietFoods[].intake" type NUMBER means "섭취량",
                                "data.dietFoods[].nutrition" type OBJECT means "개별 음식의 영양성분",
                                "data.dietFoods[].nutrition.calories" type NUMBER means "칼로리 (kcal)",
                                "data.dietFoods[].nutrition.carbohydrates" type NUMBER means "탄수화물 (g)",
                                "data.dietFoods[].nutrition.protein" type NUMBER means "단백질 (g)",
                                "data.dietFoods[].nutrition.fat" type NUMBER means "지방 (g)"
                            )
                        )
                    )
                )
                .status(HttpStatus.CREATED)
        }
        
        private fun mockCreateDietRequest(): DietV1Request.Create {
            return DietV1RequestFixture.Create().create()
        }
    }

    @Nested
    @DisplayName("PUT /v1/diets/{dietId} - 식단 수정")
    inner class UpdateTest {
        @Test
        fun success() {
            // given
            val request = DietV1RequestFixture.Update().create()
            every { dietService.update(any(), any(), any()) } returns mockDetailsResponse()

            given()
                .header("X-USER-ID", "1")
                .body(JsonUtils.stringify(request))
                .put("/v1/diets/{dietId}", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("UpdateTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.DIET.tagName)
                            .description(Tags.DIET.descriptionWith("수정")),
                        requestFields(
                            "mealType" type STRING means "식사 유형" withEnum Diet.MealType::class,
                            "consumedAt" type STRING means "섭취 시간",
                            "dietFoods" type ARRAY means "섭취한 음식 목록",
                            "dietFoods[].foodId" type NUMBER means "음식 ID",
                            "dietFoods[].intake" type NUMBER means "섭취량"
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data.dietId" type NUMBER means "생성된 식단 ID",
                                "data.userId" type NUMBER means "사용자 ID",
                                "data.mealType" type STRING means "식사 유형" withEnum Diet.MealType::class,
                                "data.consumeDate" type STRING means "섭취 날짜 (yyyy-MM-dd)",
                                "data.consumedAt" type STRING means "섭취 시간",
                                "data.totalNutrition" type OBJECT means "총 영양성분",
                                "data.totalNutrition.calories" type NUMBER means "총 칼로리 (kcal)",
                                "data.totalNutrition.carbohydrates" type NUMBER means "총 탄수화물 (g)",
                                "data.totalNutrition.protein" type NUMBER means "총 단백질 (g)",
                                "data.totalNutrition.fat" type NUMBER means "총 지방 (g)",
                                "data.dietFoods" type ARRAY means "섭취한 음식 목록",
                                "data.dietFoods[].id" type NUMBER means "식단 음식 ID",
                                "data.dietFoods[].foodId" type NUMBER means "음식 ID",
                                "data.dietFoods[].foodName" type STRING means "음식 이름",
                                "data.dietFoods[].intake" type NUMBER means "섭취량",
                                "data.dietFoods[].nutrition" type OBJECT means "개별 음식의 영양성분",
                                "data.dietFoods[].nutrition.calories" type NUMBER means "칼로리 (kcal)",
                                "data.dietFoods[].nutrition.carbohydrates" type NUMBER means "탄수화물 (g)",
                                "data.dietFoods[].nutrition.protein" type NUMBER means "단백질 (g)",
                                "data.dietFoods[].nutrition.fat" type NUMBER means "지방 (g)"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }

    private fun mockDetailsResponse(): DietV1Response.Details {
        return DietV1Response.Details(
            dietId = 1L,
            userId = 1L,
            mealType = Diet.MealType.DINNER,
            consumeDate = LocalDate.of(2025, 1, 15),
            consumedAt = LocalDateTime.of(2025, 1, 15, 19, 30),
            totalNutrition = NutritionInfo(
                calories = 245.0,
                carbohydrates = 15.0,
                protein = 25.0,
                fat = 8.0
            ),
            dietFoods = listOf(
                DietV1Response.Details.DietFood(
                    id = 1L,
                    foodId = 1L,
                    foodName = "닭가슴살",
                    intake = 1,
                    nutrition = NutritionInfo(
                        calories = 120.0,
                        carbohydrates = 0.0,
                        protein = 23.0,
                        fat = 1.2
                    )
                ),
                DietV1Response.Details.DietFood(
                    id = 2L,
                    foodId = 2L,
                    foodName = "현미밥",
                    intake = 2,
                    nutrition = NutritionInfo(
                        calories = 125.0,
                        carbohydrates = 15.0,
                        protein = 2.0,
                        fat = 6.8
                    )
                )
            )
        )
    }

    private fun mockSummaryResponse(): List<DietV1Response.Summary> {
        return listOf(
            DietV1Response.Summary(
                dietId = 1L,
                mealType = Diet.MealType.BREAKFAST,
                consumeDate = LocalDate.now(),
                consumedAt = LocalDateTime.now(),
                items = listOf(
                    DietV1Response.DietFoodResponse(
                        foodId = 1L,
                        foodName = "오트밀",
                        intake = 1,
                        unit = "컵",
                        calories = 150.0,
                        carbohydrates = 30.0,
                        protein = 5.0,
                        fat = 3.0
                    ),
                    DietV1Response.DietFoodResponse(
                        foodId = 2L,
                        foodName = "바나나",
                        intake = 1,
                        unit = "개",
                        calories = 100.0,
                        carbohydrates = 25.0,
                        protein = 1.0,
                        fat = 0.3
                    )
                )
            ),
            DietV1Response.Summary(
                dietId = 2L,
                mealType = Diet.MealType.LUNCH,
                consumeDate = LocalDate.now(),
                consumedAt = LocalDateTime.now(),
                items = listOf(
                    DietV1Response.DietFoodResponse(
                        foodId = 3L,
                        foodName = "닭가슴살",
                        intake = 100,
                        unit = "g",
                        calories = 165.0,
                        carbohydrates = 0.0,
                        protein = 31.0,
                        fat = 3.6
                    ),
                    DietV1Response.DietFoodResponse(
                        foodId = 4L,
                        foodName = "현미밥",
                        intake = 1,
                        unit = "공기",
                        calories = 218.0,
                        carbohydrates = 45.0,
                        protein = 4.5,
                        fat = 1.8
                    )
                )
            )
        )
    }
}