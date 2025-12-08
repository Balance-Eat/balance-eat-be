package org.balanceeat.domain.reminder

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.DayOfWeek
import java.time.LocalTime

interface ReminderRepositoryCustom {
    fun findAllActiveBy(dayOfWeek: DayOfWeek, time: LocalTime, pageable: Pageable): List<Reminder>
    fun countAllActiveBy(dayOfWeek: DayOfWeek, time: LocalTime): Long
}
