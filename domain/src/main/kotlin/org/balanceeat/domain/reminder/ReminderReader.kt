package org.balanceeat.domain.reminder

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ReminderReader(
    private val reminderRepository: ReminderRepository
): BaseReader<Reminder, ReminderResult>(reminderRepository, ReminderResult) {

    override fun findByIdOrThrow(id: Long): ReminderResult {
        return findByIdOrThrow(id, DomainStatus.REMINDER_NOT_FOUND)
    }
}