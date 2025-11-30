package org.balanceeat.api.reminder

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.balanceeat.domain.reminder.ReminderResult
import java.time.LocalDateTime

class ReminderV1Request {
    data class Create(
        @field:NotBlank(message = "리마인더 내용은 필수입니다")
        @field:Size(max = 500, message = "리마인더 내용은 500자를 초과할 수 없습니다")
        val content: String,

        @field:NotNull(message = "발송 시각은 필수입니다")
        val sendDatetime: LocalDateTime
    )
}

class ReminderV1Response {
    data class Details(
        val id: Long,
        val userId: Long,
        val content: String,
        val sendDatetime: LocalDateTime,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    ) {
        companion object {
            fun from(result: ReminderResult) = Details(
                id = result.id,
                userId = result.userId,
                content = result.content,
                sendDatetime = result.sendDatetime,
                createdAt = result.createdAt,
                updatedAt = result.updatedAt
            )
        }
    }
}
