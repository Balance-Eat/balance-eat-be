package org.balanceeat.admin.api.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.apibase.response.ApiResponse as CustomApiResponse

@Tag(name = "Admin User V1", description = "어드민 유저 관리 API")
interface AdminUserV1ApiSpec {

    @Operation(summary = "유저 수정", description = "어드민이 유저 정보를 수정합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "유저 수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
        ]
    )
    fun update(
        @Parameter(description = "유저 ID", required = true)
        id: Long,
        @Parameter(description = "유저 수정 요청", required = true)
        request: AdminUserV1Request.Update
    ): CustomApiResponse<AdminUserV1Response.Info>

    @Operation(summary = "유저 상세 조회", description = "어드민이 유저 상세 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "유저 조회 성공"),
            ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
        ]
    )
    fun getDetails(
        @Parameter(description = "유저 ID", required = true)
        id: Long
    ): CustomApiResponse<AdminUserV1Response.Info>
}