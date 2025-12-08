package org.balanceeat.domain.reminder

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.DayOfWeek
import java.time.LocalTime

interface ReminderRepository : JpaRepository<Reminder, Long>, ReminderRepositoryCustom {
    fun findByUserId(userId: Long): List<Reminder>
    fun findByUserIdAndId(userId: Long, id: Long): Reminder?
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Reminder>
}
