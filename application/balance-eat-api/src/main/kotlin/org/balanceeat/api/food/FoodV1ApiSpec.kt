package org.balanceeat.api.food

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.apibase.response.ApiResponse

@Tag(name = "음식 API V1")
interface FoodV1ApiSpec {
    @Operation(summary = "음식 생성")
    fun create(request: FoodV1Request.Create): ApiResponse<FoodV1Response.Info>

    @Operation(summary = "음식 수정")
    fun update(id: Long, request: FoodV1Request.Update): ApiResponse<FoodV1Response.Info>
    
    @Operation(summary = "음식 정보 조회")
    fun getFood(
        @Parameter(description = "음식 ID", required = true)
        id: Long
    ): ApiResponse<FoodV1Response.Info>
}