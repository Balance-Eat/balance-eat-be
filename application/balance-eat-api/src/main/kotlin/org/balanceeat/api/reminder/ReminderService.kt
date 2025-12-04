package org.balanceeat.api.reminder

import org.balanceeat.apibase.ApplicationStatus.REMINDER_NOT_FOUND
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.apibase.response.PageResponse
import org.balanceeat.domain.reminder.ReminderCommand
import org.balanceeat.domain.reminder.ReminderReader
import org.balanceeat.domain.reminder.ReminderWriter
import org.springframework.data.domain.Pageable
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
                sendTime = request.sendTime.withSecond(0).withNano(0),
                isActive = request.isActive,
                dayOfWeeks = request.dayOfWeeks
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
                sendTime = request.sendTime,
                isActive = request.isActive,
                dayOfWeeks = request.dayOfWeeks
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

    @Transactional(readOnly = true)
    fun getDetail(reminderId: Long, userId: Long): ReminderV1Response.Details {
        val reminder = reminderReader.findById(reminderId)
            ?.takeIf { it.userId == userId }
            ?: throw NotFoundException(REMINDER_NOT_FOUND)

        return ReminderV1Response.Details.from(reminder)
    }

    @Transactional(readOnly = true)
    fun getSummaries(userId: Long, pageable: Pageable): PageResponse<ReminderV1Response.Summary> {
        val reminders = reminderReader.findAllByUserId(userId, pageable)
        val pageResult = reminders.map { ReminderV1Response.Summary.from(it) }
        return PageResponse.from(pageResult)
    }

    @Transactional
    fun updateActivation(request: ReminderV1Request.UpdateActivation, reminderId: Long, userId: Long): ReminderV1Response.Details {
        val reminder = reminderReader.findById(reminderId)
            ?.takeIf { it.userId == userId }
            ?: throw NotFoundException(REMINDER_NOT_FOUND)

        val result = reminderWriter.updateActivation(
            command = ReminderCommand.UpdateActivation(
                id = reminder.id,
                isActive = request.isActive
            )
        )

        return ReminderV1Response.Details.from(result)
    }
}
