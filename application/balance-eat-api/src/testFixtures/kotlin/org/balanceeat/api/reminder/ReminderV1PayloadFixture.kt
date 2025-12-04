package org.balanceeat.api.reminder

import org.balanceeat.common.TestFixture
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderV1RequestFixture {
    data class Create(
        var content: String = "아침 식사 기록하기",
        var sendTime: LocalTime = LocalTime.of(9, 0, 0),
        var isActive: Boolean = true,
        var dayOfWeeks: List<DayOfWeek> = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY) // 월, 수, 금
    ) : TestFixture<ReminderV1Request.Create> {
        override fun create(): ReminderV1Request.Create {
            return ReminderV1Request.Create(
                content = content,
                sendTime = sendTime,
                isActive = isActive,
                dayOfWeeks = dayOfWeeks
            )
        }
    }

    data class Update(
        var content: String = "수정된 리마인더 내용",
        var sendTime: LocalTime = LocalTime.of(10, 0, 0),
        var isActive: Boolean = false,
        var dayOfWeeks: List<DayOfWeek> = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY) // 화, 목
    ) : TestFixture<ReminderV1Request.Update> {
        override fun create(): ReminderV1Request.Update {
            return ReminderV1Request.Update(
                content = content,
                sendTime = sendTime,
                isActive = isActive,
                dayOfWeeks = dayOfWeeks
            )
        }
    }

    data class UpdateActivation(
        var isActive: Boolean = false
    ) : TestFixture<ReminderV1Request.UpdateActivation> {
        override fun create(): ReminderV1Request.UpdateActivation {
            return ReminderV1Request.UpdateActivation(
                isActive = isActive
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

fun reminderUpdateActivationV1RequestFixture(block: ReminderV1RequestFixture.UpdateActivation.() -> Unit = {}): ReminderV1Request.UpdateActivation {
    return ReminderV1RequestFixture.UpdateActivation().apply(block).create()
}
