package org.balanceeat.api.statistics

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.api.statistics.StatisticsV1Response.StatisticsType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.queryParameters

@WebMvcTest(StatisticsV1Controller::class)
class StatisticsV1ControllerTest : ControllerTestContext() {

    @Nested
    @DisplayName("GET /v1/stats - 통계 조회")
    inner class GetStatisticsTest {

        @Test
        @DisplayName("통계 조회 성공")
        fun success() {
            given()
                .header("X-USER-ID", "1")
                .params("type", "DAILY")
                .get("/v1/stats")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("GetStatisticsTest"),
                        ResourceSnippetParametersBuilder()
                            .tag(Tags.STATS.tagName)
                            .description(Tags.STATS.descriptionWith("조회")),
                        queryParameters(
                            "type" queryMeans "통계 유형" withEnum StatisticsType::class
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data" type ARRAY means "통계 목록",
                                "data[].type" type STRING means "통계 유형" withEnum StatisticsType::class,
                                "data[].date" type STRING means "기준 날짜 (yyyy-MM-dd)",
                                "data[].totalCalories" type NUMBER means "총 칼로리 (kcal)",
                                "data[].totalCarbohydrates" type NUMBER means "총 탄수화물 (g)",
                                "data[].totalProtein" type NUMBER means "총 단백질 (g)",
                                "data[].totalFat" type NUMBER means "총 지방 (g)"
                            )
                        )
                    )
                )
                .status(HttpStatus.OK)
        }
    }
}
