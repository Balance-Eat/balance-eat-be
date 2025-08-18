package org.balanceeat.api.diet

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.api.config.ApiResponse
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Tag(name = "식사 기록 API V1")
interface DietV1ApiSpec {

//    @Operation(summary = "날짜별 식사 기록 조회")
//    fun getDietsByDate(
//        @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", required = true, example = "2024-01-15")
//        date: LocalDate,
//        @Parameter(description = "사용자 ID (선택)", required = false)
//        userId: Long?,
//        @Parameter(description = "사용자 UUID (선택)", required = false)
//        userUuid: String?
//    ): ApiResponse<List<DietV1Response.Info>>
}