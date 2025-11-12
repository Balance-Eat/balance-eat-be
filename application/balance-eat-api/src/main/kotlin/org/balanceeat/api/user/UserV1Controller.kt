package org.balanceeat.api.user

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/users")
class UserV1Controller(
    private val userService: UserService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: UserV1Request.Create
    ): ApiResponse<UserV1Response.Details> {
        return ApiResponse.success(
            userService.create(request)
        )
    }

    @GetMapping("/me")
    fun getMe(
        @RequestParam(required = false) uuid: String
    ): ApiResponse<UserV1Response.Details> {
        return ApiResponse.success(
            userService.findByUuid(uuid)
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: UserV1Request.Update
    ): ApiResponse<UserV1Response.Details> {
        return ApiResponse.success(
            userService.update(id, request)
        )
    }
}
