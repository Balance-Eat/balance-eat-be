package org.balanceeat.api.diet

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import org.balanceeat.api.config.supports.ControllerTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.queryParameters

@WebMvcTest(DietV1Controller::class)
class DietV1ControllerTest: ControllerTestContext() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

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
}