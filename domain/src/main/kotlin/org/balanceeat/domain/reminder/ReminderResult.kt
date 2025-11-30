package org.balanceeat.domain.reminder

import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDateTime

data class ReminderResult(
    val id: Long,
    val userId: Long,
    val content: String,
    val sendDatetime: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object : EntityMapper<Reminder, ReminderResult> {
        override fun from(entity: Reminder): ReminderResult {
            return ReminderResult(
                id = entity.id,
                userId = entity.userId,
                content = entity.content,
                sendDatetime = entity.sendDatetime,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
