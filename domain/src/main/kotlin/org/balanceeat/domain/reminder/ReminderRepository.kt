package org.balanceeat.domain.reminder

import org.springframework.data.jpa.repository.JpaRepository

interface ReminderRepository : JpaRepository<Reminder, Long> {
    fun findByUserId(userId: Long): List<Reminder>
    fun findByUserIdAndId(userId: Long, id: Long): Reminder?
}
