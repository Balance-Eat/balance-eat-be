package org.balanceeat.admin.api.user

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/v1/users")
class AdminUserV1Controller(
    private val adminUserService: AdminUserService,
) {

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: AdminUserV1Request.Update
    ): ApiResponse<AdminUserV1Response.Details> {
        return ApiResponse.success(
            adminUserService.update(request, id)
        )
    }

    @GetMapping("/{id}")
    fun getDetails(
        @PathVariable id: Long
    ): ApiResponse<AdminUserV1Response.Details> {
        return ApiResponse.success(
            adminUserService.getDetails(id)
        )
    }
}