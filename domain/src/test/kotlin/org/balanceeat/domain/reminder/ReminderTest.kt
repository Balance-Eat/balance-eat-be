package org.balanceeat.domain.reminder

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

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
            val sendDatetimeWithSeconds = LocalDateTime.of(2025, 12, 2, 9, 0, 30)
            val reminder = reminderFixture { sendDatetime = sendDatetimeWithSeconds }

            // when & then
            assertThatThrownBy { reminder.guard() }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("리마인더 전송 시간의 초는 0이어야 합니다")
        }
    }

    @Nested
    @DisplayName("update 메서드 테스트")
    inner class UpdateTest {

        @Test
        fun `리마인더 내용과 발송 시각을 수정할 수 있다`() {
            // given
            val reminder = reminderFixture {
                content = "기존 내용"
                sendDatetime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
            }
            val newContent = "새로운 내용"
            val newSendDatetime = LocalDateTime.of(2025, 12, 3, 10, 30, 15) // Seconds will be truncated

            // when
            reminder.update(newContent, newSendDatetime)

            // then
            assertThat(reminder.content).isEqualTo(newContent)
            assertThat(reminder.sendDatetime).isEqualTo(LocalDateTime.of(2025, 12, 3, 10, 30, 0))
        }

        @Test
        fun `리마인더 내용만 수정할 수 있다`() {
            // given
            val reminder = reminderFixture {
                content = "기존 내용"
                sendDatetime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
            }
            val newContent = "새로운 내용만"

            // when
            reminder.update(newContent, null)

            // then
            assertThat(reminder.content).isEqualTo(newContent)
            assertThat(reminder.sendDatetime).isEqualTo(LocalDateTime.of(2025, 12, 2, 9, 0, 0)) // Should remain unchanged
        }

        @Test
        fun `리마인더 발송 시각만 수정할 수 있다`() {
            // given
            val reminder = reminderFixture {
                content = "기존 내용"
                sendDatetime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
            }
            val newSendDatetime = LocalDateTime.of(2025, 12, 4, 11, 45, 59) // Seconds will be truncated

            // when
            reminder.update(null, newSendDatetime)

            // then
            assertThat(reminder.content).isEqualTo("기존 내용") // Should remain unchanged
            assertThat(reminder.sendDatetime).isEqualTo(LocalDateTime.of(2025, 12, 4, 11, 45, 0))
        }
    }
}
