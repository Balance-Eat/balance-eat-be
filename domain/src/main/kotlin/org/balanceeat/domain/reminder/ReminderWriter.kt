package org.balanceeat.domain.reminder

import org.balanceeat.domain.common.DomainStatus.REMINDER_NOT_FOUND
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReminderWriter(
    private val reminderRepository: ReminderRepository
) {
    @Transactional
    fun create(command: ReminderCommand.Create): ReminderResult {
        val reminder = Reminder(
            userId = command.userId,
            content = command.content,
            sendDatetime = command.sendDatetime.withSecond(0).withNano(0)
        )

        val savedReminder = reminderRepository.save(reminder)
        return ReminderResult.from(savedReminder)
    }

    @Transactional
    fun update(command: ReminderCommand.Update): ReminderResult {
        val reminder = reminderRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(REMINDER_NOT_FOUND) }

        reminder.update(
            content = command.content,
            sendDatetime = command.sendDatetime
        )

        val savedReminder = reminderRepository.save(reminder)
        return ReminderResult.from(savedReminder)
    }

    @Transactional
    fun delete(id: Long) {
        reminderRepository.findById(id)
            .ifPresent { reminderRepository.delete(it) }
    }
}
