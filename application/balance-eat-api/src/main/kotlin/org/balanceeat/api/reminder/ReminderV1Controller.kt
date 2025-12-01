package org.balanceeat.api.reminder

import jakarta.validation.Valid
import org.balanceeat.apibase.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/reminders")
class ReminderV1Controller(
    private val reminderService: ReminderService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody @Valid request: ReminderV1Request.Create
    ): ApiResponse<ReminderV1Response.Details> {
        val result = reminderService.create(request, userId)
        return ApiResponse.success(result)
    }

    @GetMapping("/{reminderId}")
    fun getDetail(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable reminderId: Long
    ): ApiResponse<ReminderV1Response.Details> {
        val result = reminderService.getDetail(reminderId, userId)
        return ApiResponse.success(result)
    }

    @PutMapping("/{reminderId}")
    fun update(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable reminderId: Long,
        @RequestBody @Valid request: ReminderV1Request.Update
    ): ApiResponse<ReminderV1Response.Details> {
        val result = reminderService.update(request, reminderId, userId)
        return ApiResponse.success(result)
    }

    @DeleteMapping("/{reminderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable reminderId: Long
    ) {
        reminderService.delete(reminderId, userId)
    }
}
