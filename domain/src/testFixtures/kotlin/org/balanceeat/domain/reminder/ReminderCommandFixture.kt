package org.balanceeat.domain.reminder

import org.balanceeat.common.TestFixture
import java.time.LocalDateTime

class ReminderCommandFixture {

    class Create(
        var userId: Long = 1L,
        var content: String = "아침 식사 기록하기",
        var sendDatetime: LocalDateTime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
    ) : TestFixture<ReminderCommand.Create> {
        override fun create(): ReminderCommand.Create {
            return ReminderCommand.Create(
                userId = userId,
                content = content,
                sendDatetime = sendDatetime
            )
        }
    }

    class Update(
        var id: Long = 1L,
        var content: String? = "수정된 리마인더 내용",
        var sendDatetime: LocalDateTime? = LocalDateTime.of(2025, 12, 2, 10, 0, 0)
    ) : TestFixture<ReminderCommand.Update> {
        override fun create(): ReminderCommand.Update {
            return ReminderCommand.Update(
                id = id,
                content = content,
                sendDatetime = sendDatetime
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
