package org.balanceeat.domain.reminder

import org.balanceeat.common.TestFixture
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderCommandFixture {

    class Create(
        var userId: Long = 1L,
        var content: String = "아침 식사 기록하기",
        var sendTime: LocalTime = LocalTime.of(9, 0, 0),
        var isActive: Boolean = true,
        var dayOfWeeks: List<DayOfWeek> = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
    ) : TestFixture<ReminderCommand.Create> {
        override fun create(): ReminderCommand.Create {
            return ReminderCommand.Create(
                userId = userId,
                content = content,
                sendTime = sendTime,
                isActive = isActive,
                dayOfWeeks = dayOfWeeks
            )
        }
    }

    class Update(
        var id: Long = 1L,
        var content: String = "수정된 리마인더 내용",
        var sendTime: LocalTime = LocalTime.of(10, 0, 0),
        var isActive: Boolean = true,
        var dayOfWeeks: List<DayOfWeek> = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
    ) : TestFixture<ReminderCommand.Update> {
        override fun create(): ReminderCommand.Update {
            return ReminderCommand.Update(
                id = id,
                content = content,
                sendTime = sendTime,
                isActive = isActive,
                dayOfWeeks = dayOfWeeks
            )
        }
    }
}

fun reminderCreateCommandFixture(block: ReminderCommandFixture.Create.() -> Unit = {}): ReminderCommand.Create {
    return ReminderCommandFixture.Create().apply(block).create()
}

fun reminderUpdateCommandFixture(block: ReminderCommandFixture.Update.() -> Unit = {}): ReminderCommand.Update {
    return ReminderCommandFixture.Update().apply(block).create()
}
