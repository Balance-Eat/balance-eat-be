package org.balanceeat.domain.reminder

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.DayOfWeek
import java.time.LocalTime

class ReminderWriterTest : IntegrationTestContext() {

    @Autowired
    private lateinit var reminderWriter: ReminderWriter

    @Autowired
    private lateinit var reminderRepository: ReminderRepository

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {
        @Test
        fun `리마인더를 생성할 수 있다`() {
            // given
            val command = reminderCreateCommandFixture()

            // when
            val result = reminderWriter.create(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {
        @Test
        fun `리마인더를 수정할 수 있다`() {
            // given
            val reminder = createEntity(
                reminderFixture {
                    userId = 1L
                    content = "수정 전 내용"
                    sendTime = LocalTime.of(9, 0, 0)
                    isActive = true
                    dayOfWeeks = listOf(DayOfWeek.MONDAY)
                }
            )

            val command = reminderUpdateCommandFixture {
                id = reminder.id
                content = "수정 후 내용"
                sendTime = LocalTime.of(10, 0, 0)
                isActive = false
                dayOfWeeks = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
            }

            // when
            val result = reminderWriter.update(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }

    @Nested
    @DisplayName("삭제 테스트")
    inner class DeleteTest {
        @Test
        fun `리마인더를 삭제할 수 있다`() {
            // given
            val reminder = createEntity(reminderFixture())

            // when
            reminderWriter.delete(reminder.id)

            // then
            assertThat(reminderRepository.findById(reminder.id)).isEmpty
        }
    }

    @Nested
    @DisplayName("활성화 상태 변경 테스트")
    inner class UpdateActivationTest {
        @Test
        fun `리마인더 활성화 상태를 변경할 수 있다`() {
            // given
            val reminder = createEntity(
                reminderFixture {
                    userId = 1L
                    isActive = true
                }
            )

            val command = reminderUpdateActivationCommandFixture {
                id = reminder.id
                isActive = false
            }

            // when
            val result = reminderWriter.updateActivation(command)

            // then
            assertThat(result.isActive).isFalse()
            assertThat(result.id).isEqualTo(reminder.id)
        }

        @Test
        fun `비활성화된 리마인더를 활성화할 수 있다`() {
            // given
            val reminder = createEntity(
                reminderFixture {
                    userId = 1L
                    isActive = false
                }
            )

            val command = reminderUpdateActivationCommandFixture {
                id = reminder.id
                isActive = true
            }

            // when
            val result = reminderWriter.updateActivation(command)

            // then
            assertThat(result.isActive).isTrue()
            assertThat(result.id).isEqualTo(reminder.id)
        }
    }
}
