package org.balanceeat.admin.api.food

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/v1/foods")
class AdminFoodV1Controller(
    private val adminFoodService: AdminFoodService,
) {

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long,
                        @RequestBody @Valid request: AdminFoodV1Request.Update): ApiResponse<AdminFoodV1Response.Details> {
        return ApiResponse.success(
            adminFoodService.update(request, id, 1L) // TODO: 관리자 인증 연동 후 수정
        )
    }

    @GetMapping("/{id}")
    fun getDetails(@PathVariable id: Long): ApiResponse<AdminFoodV1Response.Details> {
        return ApiResponse.success(
            adminFoodService.getDetails(id)
        )
    }
}