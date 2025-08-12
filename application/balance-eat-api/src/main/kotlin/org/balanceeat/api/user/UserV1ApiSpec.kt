package org.balanceeat.api.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.balanceeat.api.config.ApiResponse

@Tag(name = "유저 API V1")
interface UserV1ApiSpec {
    @Operation(summary = "사용자 생성")
    fun create(request: UserV1Request.Create): ApiResponse<Void>
} 
