package org.balanceeat.domain.reminder

import java.time.DayOfWeek
import java.time.LocalTime

class ReminderCommand {

    data class Create(
        val userId: Long,
        val content: String,
        val sendTime: LocalTime,
        val isActive: Boolean = true,
        val dayOfWeeks: List<DayOfWeek>
    )

    data class Update(
        val id: Long,
        val content: String,
        val sendTime: LocalTime,
        val isActive: Boolean,
        val dayOfWeeks: List<DayOfWeek>
    )

    data class UpdateActivation(
        val id: Long,
        val isActive: Boolean
    )
}
