package org.balanceeat.domain.reminder

import org.balanceeat.common.TestFixture
import java.time.LocalDateTime

class ReminderFixture(
    var id: Long = 1L,
    var userId: Long = 1L,
    var content: String = "아침 식사 기록하기",
    var sendDatetime: LocalDateTime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
) : TestFixture<Reminder> {
    override fun create(): Reminder {
        return Reminder(
            id = 0L,
            userId = userId,
            content = content,
            sendDatetime = sendDatetime
        )
    }
}

fun reminderFixture(block: ReminderFixture.() -> Unit = {}): Reminder {
    return ReminderFixture().apply(block).create()
}
