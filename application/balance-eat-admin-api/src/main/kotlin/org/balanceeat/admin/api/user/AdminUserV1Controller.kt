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
    fun update(@PathVariable id: Long,
                        @RequestBody @Valid request: AdminUserV1Request.Update): ApiResponse<AdminUserV1Response.Info> {
        val result = adminUserService.update(request, id, 1L) // TODO: 관리자 인증 연동 후 수정
        return ApiResponse.success(
            AdminUserV1Response.Info.from(result)
        )
    }

    @GetMapping("/{id}")
    fun getDetails(@PathVariable id: Long): ApiResponse<AdminUserV1Response.Info> {
        val user = adminUserService.getDetails(id)
        return ApiResponse.success(AdminUserV1Response.Info.from(user))
    }
}