package org.balanceeat.api.reminder

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.balanceeat.domain.reminder.ReminderResult
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class ReminderV1Request {
    data class Create(
        @field:NotBlank(message = "리마인더 내용은 필수입니다")
        @field:Size(max = 500, message = "리마인더 내용은 500자를 초과할 수 없습니다")
        val content: String,

        @field:NotNull(message = "발송 시각은 필수입니다")
        val sendTime: LocalTime,

        val isActive: Boolean = true,

        @field:NotEmpty(message = "알림 요일은 최소 1개 이상 선택해야 합니다")
        val dayOfWeeks: List<DayOfWeek>
    )

    data class Update(
        @field:NotBlank(message = "리마인더 내용은 필수입니다")
        @field:Size(max = 500, message = "리마인더 내용은 500자를 초과할 수 없습니다")
        val content: String,

        @field:NotNull(message = "발송 시각은 필수입니다")
        val sendTime: LocalTime,

        @field:NotNull(message = "활성 상태는 필수입니다")
        val isActive: Boolean,

        @field:NotEmpty(message = "알림 요일은 최소 1개 이상 선택해야 합니다")
        val dayOfWeeks: List<DayOfWeek>
    )
}

class ReminderV1Response {
    data class Details(
        val id: Long,
        val userId: Long,
        val content: String,
        val sendTime: LocalTime,
        val isActive: Boolean,
        val dayOfWeeks: List<DayOfWeek>,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    ) {
        companion object {
            fun from(result: ReminderResult) = Details(
                id = result.id,
                userId = result.userId,
                content = result.content,
                sendTime = result.sendTime,
                isActive = result.isActive,
                dayOfWeeks = result.dayOfWeeks,
                createdAt = result.createdAt,
                updatedAt = result.updatedAt
            )
        }
    }

    data class Summary(
        val id: Long,
        val content: String,
        val sendTime: LocalTime,
        val isActive: Boolean,
        val dayOfWeeks: List<DayOfWeek>
    ) {
        companion object {
            fun from(result: ReminderResult) = Summary(
                id = result.id,
                content = result.content,
                sendTime = result.sendTime,
                isActive = result.isActive,
                dayOfWeeks = result.dayOfWeeks
            )
        }
    }
}
