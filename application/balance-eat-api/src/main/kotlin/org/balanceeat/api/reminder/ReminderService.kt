package org.balanceeat.api.reminder

import org.balanceeat.domain.reminder.ReminderCommand
import org.balanceeat.domain.reminder.ReminderWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReminderService(
    private val reminderWriter: ReminderWriter
) {
    @Transactional
    fun create(request: ReminderV1Request.Create, userId: Long): ReminderV1Response.Details {
        val result = reminderWriter.create(
            command = ReminderCommand.Create(
                userId = userId,
                content = request.content,
                sendDatetime = request.sendDatetime
            )
        )

        return ReminderV1Response.Details.from(result)
    }
}
