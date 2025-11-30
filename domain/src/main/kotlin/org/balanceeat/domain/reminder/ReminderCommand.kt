package org.balanceeat.domain.reminder

import java.time.LocalDateTime

class ReminderCommand {

    data class Create(
        val userId: Long,
        val content: String,
        val sendDatetime: LocalDateTime
    )

    data class Update(
        val id: Long,
        val content: String,
        val sendDatetime: LocalDateTime
    )
}
