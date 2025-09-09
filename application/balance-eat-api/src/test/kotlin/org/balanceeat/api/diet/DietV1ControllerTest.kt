package org.balanceeat.api.diet

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.diet.Diet
import org.balanceeat.domain.diet.NutritionInfo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
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
            given()
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
                                "data.dailyTotal" type OBJECT means "해당 날짜의 총 영양 정보",
                                "data.dailyTotal.totalCalorie" type NUMBER means "총 칼로리 (kcal)",
                                "data.dailyTotal.totalCarbohydrates" type NUMBER means "총 탄수화물 (g)",
                                "data.dailyTotal.totalProtein" type NUMBER means "총 단백질 (g)",
                                "data.dailyTotal.totalFat" type NUMBER means "총 지방 (g)",
                                "data.diets" type ARRAY means "식사 기록 목록",
                                "data.diets[].dietId" type NUMBER means "식사 기록 고유 ID (DIET 테이블의 id)",
                                "data.diets[].eatingAt" type STRING means "식사 시간 (ISO 8601)",
                                "data.diets[].type" type STRING means "식사 유형 (아침, 점심, 저녁, 간식 등)",
                                "data.diets[].items" type ARRAY means "해당 식사에 포함된 음식 목록",
                                "data.diets[].items[].foodId" type NUMBER means "음식 고유 ID (FOOD 테이블의 id)",
                                "data.diets[].items[].foodName" type STRING means "음식 이름 (FOOD 테이블의 name)",
                                "data.diets[].items[].intake" type NUMBER means "섭취량 (DIET_ITEM 테이블의 intake)",
                                "data.diets[].items[].unit" type STRING means "섭취량 단위 (FOOD 테이블의 unit)",
                                "data.diets[].items[].calories" type NUMBER means "해당 섭취량의 칼로리 (계산된 값)",
                                "data.diets[].items[].carbohydrates" type NUMBER means "해당 섭취량의 탄수화물 (계산된 값)",
                                "data.diets[].items[].protein" type NUMBER means "해당 섭취량의 단백질 (계산된 값)",
                                "data.diets[].items[].fat" type NUMBER means "해당 섭취량의 지방 (계산된 값)"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }
    
    @Nested
    @DisplayName("POST /v1/diets - 식단 생성")
    inner class CreateDietTest {
        @Test
        fun success() {
            // given
            val request = mockCreateDietRequest()
            every { dietService.create(any(), any()) } returns mockDietResponse()
            
            given()
                .header("X-USER-ID", "1")
                .body(request)
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
            return DietV1Request.Create(
                mealType = "DINNER",
                consumedAt = LocalDateTime.of(2025, 1, 15, 19, 30),
                dietFoods = listOf(
                    DietV1Request.Create.Companion.DietFood(
                        foodId = 1L,
                        intake = 1
                    ),
                    DietV1Request.Create.Companion.DietFood(
                        foodId = 2L,
                        intake = 2
                    )
                )
            )
        }
    }
    
    private fun mockDietResponse(): DietV1Response.Details {
        return DietV1Response.Details(
            dietId = 1L,
            userId = 1L,
            mealType = Diet.MealType.DINNER,
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
}