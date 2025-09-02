package org.balanceeat.admin.api.food

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/v1/foods")
class AdminFoodV1Controller(
    private val adminFoodService: AdminFoodService,
) : AdminFoodV1ApiSpec {

    @PutMapping("/{id}")
    override fun update(@PathVariable id: Long,
                        @RequestBody @Valid request: AdminFoodV1Request.Update): ApiResponse<AdminFoodV1Response.Info> {
        val result = adminFoodService.update(request, id, 1L) // TODO: 관리자 인증 연동 후 수정
        return ApiResponse.success(
            AdminFoodV1Response.Info.from(result)
        )
    }

    @GetMapping("/{id}")
    override fun getDetails(@PathVariable id: Long): ApiResponse<AdminFoodV1Response.Info> {
        val food = adminFoodService.getDetails(id)
        return ApiResponse.success(AdminFoodV1Response.Info.from(food))
    }
}