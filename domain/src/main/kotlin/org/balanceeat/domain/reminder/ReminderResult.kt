package org.balanceeat.domain.reminder

import org.balanceeat.domain.common.EntityMapper
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

data class ReminderResult(
    val id: Long,
    val userId: Long,
    val content: String,
    val sendTime: LocalTime,
    val isActive: Boolean,
    val dayOfWeeks: List<DayOfWeek>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object : EntityMapper<Reminder, ReminderResult> {
        override fun from(entity: Reminder): ReminderResult {
            return ReminderResult(
                id = entity.id,
                userId = entity.userId,
                content = entity.content,
                sendTime = entity.sendTime,
                isActive = entity.isActive,
                dayOfWeeks = entity.dayOfWeeks,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
