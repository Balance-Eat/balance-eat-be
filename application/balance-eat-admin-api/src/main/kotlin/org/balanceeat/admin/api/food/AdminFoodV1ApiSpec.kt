package org.balanceeat.admin.api.food

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.apibase.response.ApiResponse

@Tag(name = "어드민 음식 API V1")
interface AdminFoodV1ApiSpec {
    @Operation(summary = "어드민 음식 수정")
    fun update(
        @Parameter(description = "음식 ID", required = true)
        id: Long,
        request: AdminFoodV1Request.Update
    ): ApiResponse<AdminFoodV1Response.Info>
    
    @Operation(summary = "어드민 음식 상세 조회")
    fun getDetails(
        @Parameter(description = "음식 ID", required = true)
        id: Long
    ): ApiResponse<AdminFoodV1Response.Info>
}