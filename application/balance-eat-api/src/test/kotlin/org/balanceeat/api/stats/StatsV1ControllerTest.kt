package org.balanceeat.api.stats

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.balanceeat.api.config.supports.ControllerTestContext
import org.balanceeat.domain.diet.StatsType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.queryParameters

@WebMvcTest(StatsV1Controller::class)
class StatsV1ControllerTest : ControllerTestContext() {
    @MockkBean(relaxed = true)
    private lateinit var statsService: StatsService

    @Nested
    @DisplayName("GET /v1/stats - 통계 조회")
    inner class GetStatisticsTest {

        @Test
        @DisplayName("통계 조회 성공")
        fun success() {
            every { statsService.getStats(any(), any(), any(), any()) } returns mockResponse()

            given()
                .header("X-USER-ID", "1")
                .params("type", "DAILY")
                .params("from", "2025-06-01")
                .params("to", "2025-06-09")
                .params("limit", "5")
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
                            "type" queryMeans "통계 유형" withEnum StatsType::class,
                            "from" queryMeans "조회 시작 날짜 (yyyy-MM-dd)" isOptional true,
                            "to" queryMeans "조회 종료 날짜 (yyyy-MM-dd)" isOptional true,
                            "limit" queryMeans "조회 개수 (기본값: 5)" isOptional true
                        ),
                        responseFields(
                            fieldsWithBasic(
                                "data" type ARRAY means "통계 목록",
                                "data[].type" type STRING means "통계 유형" withEnum StatsType::class,
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

        private fun mockResponse(): List<StatsV1Response.DietStats> {
            return listOf(
                StatsV1Response.DietStats(
                    type = StatsType.DAILY,
                    date = java.time.LocalDate.of(2025, 6, 1),
                    totalCalories = 2000.0,
                    totalCarbohydrates = 250.0,
                    totalProtein = 150.0,
                    totalFat = 70.0
                ),
                StatsV1Response.DietStats(
                    type = StatsType.DAILY,
                    date = java.time.LocalDate.of(2025, 6, 2),
                    totalCalories = 1800.0,
                    totalCarbohydrates = 220.0,
                    totalProtein = 130.0,
                    totalFat = 60.0
                )
            )
        }
    }
}
