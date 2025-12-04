package org.balanceeat.domain.reminder

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderTest {

    @Nested
    @DisplayName("guard 메서드 테스트")
    inner class GuardTest {

        @Test
        fun `리마인더 내용은 필수값이다`() {
            // given
            val reminder = reminderFixture { content = "" }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("리마인더 내용은 필수값입니다")
        }

        @Test
        fun `리마인더 내용은 500자를 초과할 수 없다`() {
            // given
            val longContent = "a".repeat(Reminder.MAX_CONTENT_LENGTH + 1)
            val reminder = reminderFixture { content = longContent }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("리마인더 내용은 ${Reminder.MAX_CONTENT_LENGTH}자를 초과할 수 없습니다")
        }

        @Test
        fun `사용자 ID는 0보다 큰 값이어야 한다`() {
            // given
            val reminder = reminderFixture { userId = 0L }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("사용자 ID는 0보다 큰 값이어야 합니다")
        }

        @Test
        fun `리마인더 전송 시간의 초는 0이어야 한다`() {
            // given
            val sendTimeWithSeconds = LocalTime.of(9, 0, 30)
            val reminder = reminderFixture { sendTime = sendTimeWithSeconds }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("리마인더 전송 시간의 초는 0이어야 합니다")
        }

        @Test
        fun `알림 요일은 최소 1개 이상 선택해야 한다`() {
            // given
            val reminder = reminderFixture { dayOfWeeks = emptyList() }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("알림 요일은 최소 1개 이상 선택해야 합니다")
        }

        @Test
        fun `중복된 요일은 선택할 수 없다`() {
            // given
            val reminder = reminderFixture { dayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.MONDAY) }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("중복된 요일은 선택할 수 없습니다")
        }
    }

    @Nested
    @DisplayName("update 메서드 테스트")
    inner class UpdateTest {

        @Test
        fun `리마인더 모든 필드를 수정할 수 있다`() {
            // given
            val reminder = reminderFixture {
                content = "기존 내용"
                sendTime = LocalTime.of(9, 0, 0)
                isActive = true
                dayOfWeeks = listOf(DayOfWeek.MONDAY)
            }
            val newContent = "새로운 내용"
            val newSendTime = LocalTime.of(10, 30, 15) // Seconds will be truncated
            val newIsActive = false
            val newDayOfWeeks = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)

            // when
            reminder.update(newContent, newSendTime, newIsActive, newDayOfWeeks)

            // then
            assertThat(reminder.content).isEqualTo(newContent)
            assertThat(reminder.sendTime).isEqualTo(newSendTime.withSecond(0))
            assertThat(reminder.isActive).isEqualTo(newIsActive)
            assertThat(reminder.dayOfWeeks).isEqualTo(newDayOfWeeks)
        }
    }
}
