package org.balanceeat.api.diet

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Tag(name = "식사 기록 API V1")
interface DietV1ApiSpec {

    @Operation(summary = "날짜별 식사 기록 조회")
    fun getDietsByDate(
        @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", required = true, example = "2025-08-04")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        date: LocalDate
    ): ApiResponse<DietV1Response.DailyDietInfo>
    
    @Operation(summary = "식단 생성")
    fun createDiet(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        userId: Long,
        @RequestBody(description = "식단 생성 요청", required = true)
        request: DietV1Request.Create
    ): ApiResponse<DietV1Response.Details>
}