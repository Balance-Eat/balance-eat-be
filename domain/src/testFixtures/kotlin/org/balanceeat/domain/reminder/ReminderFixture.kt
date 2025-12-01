package org.balanceeat.domain.reminder

import org.balanceeat.common.TestFixture
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderFixture(
    var id: Long = 1L,
    var userId: Long = 1L,
    var content: String = "아침 식사 기록하기",
    var sendTime: LocalTime = LocalTime.of(9, 0, 0),
    var isActive: Boolean = true,
    var dayOfWeeks: List<DayOfWeek> = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
) : TestFixture<Reminder> {
    override fun create(): Reminder {
        return Reminder(
            id = 0L,
            userId = userId,
            content = content,
            sendTime = sendTime,
            isActive = isActive,
            dayOfWeeks = dayOfWeeks
        )
    }
}

fun reminderFixture(block: ReminderFixture.() -> Unit = {}): Reminder {
    return ReminderFixture().apply(block).create()
}
