package org.balanceeat.api.reminder

import org.balanceeat.common.TestFixture
import java.time.LocalDateTime

class ReminderV1RequestFixture {
    data class Create(
        var content: String = "아침 식사 기록하기",
        var sendDatetime: LocalDateTime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
    ) : TestFixture<ReminderV1Request.Create> {
        override fun create(): ReminderV1Request.Create {
            return ReminderV1Request.Create(
                content = content,
                sendDatetime = sendDatetime
            )
        }
    }

    data class Update(
        var content: String = "수정된 리마인더 내용",
        var sendDatetime: LocalDateTime = LocalDateTime.of(2025, 12, 2, 10, 0, 0)
    ) : TestFixture<ReminderV1Request.Update> {
        override fun create(): ReminderV1Request.Update {
            return ReminderV1Request.Update(
                content = content,
                sendDatetime = sendDatetime
            )
        }
    }
}

fun reminderCreateV1RequestFixture(block: ReminderV1RequestFixture.Create.() -> Unit = {}): ReminderV1Request.Create {
    return ReminderV1RequestFixture.Create().apply(block).create()
}

fun reminderUpdateV1RequestFixture(block: ReminderV1RequestFixture.Update.() -> Unit = {}): ReminderV1Request.Update {
    return ReminderV1RequestFixture.Update().apply(block).create()
}
