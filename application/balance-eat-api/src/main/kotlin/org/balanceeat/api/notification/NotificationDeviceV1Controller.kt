package org.balanceeat.api.notification

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/notification-devices")
class NotificationDeviceV1Controller(
    private val notificationDeviceService: NotificationDeviceService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody @Valid request: NotificationDeviceV1Request.Create
    ): ApiResponse<NotificationDeviceV1Response.Details> {
        val result = notificationDeviceService.create(request, userId)
        return ApiResponse.success(result)
    }
}
