package org.balanceeat.api.reminder

import org.balanceeat.apibase.ApplicationStatus.REMINDER_NOT_FOUND
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.reminder.ReminderCommand
import org.balanceeat.domain.reminder.ReminderReader
import org.balanceeat.domain.reminder.ReminderWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReminderService(
    private val reminderWriter: ReminderWriter,
    private val reminderReader: ReminderReader
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

    @Transactional
    fun update(request: ReminderV1Request.Update, reminderId: Long, userId: Long): ReminderV1Response.Details {
        val reminder = reminderReader.findById(reminderId)
            ?.takeIf { it.userId == userId }
            ?: throw NotFoundException(REMINDER_NOT_FOUND)

        val result = reminderWriter.update(
            command = ReminderCommand.Update(
                id = reminder.id,
                content = request.content,
                sendDatetime = request.sendDatetime
            )
        )

        return ReminderV1Response.Details.from(result)
    }

    @Transactional
    fun delete(reminderId: Long, userId: Long) {
        val reminder = reminderReader.findById(reminderId)

        if (reminder == null) return

        if (reminder.userId != userId) {
            throw NotFoundException(REMINDER_NOT_FOUND)
        }

        reminderWriter.delete(reminder.id)
    }
}
