package org.balanceeat.api.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.apibase.response.ApiResponse

@Tag(name = "유저 API V1")
interface UserV1ApiSpec {
    @Operation(summary = "사용자 생성")
    fun create(request: UserV1Request.Create): ApiResponse<Void>
    
    @Operation(summary = "사용자 정보 조회")
    fun getMe(
        @Parameter(description = "사용자 UUID", required = false)
        uuid: String
    ): ApiResponse<UserV1Response.Info>
    
    @Operation(summary = "사용자 정보 수정")
    fun update(
        @Parameter(description = "사용자 ID", required = true)
        id: Long,
        request: UserV1Request.Update
    ): ApiResponse<Void>
} 
