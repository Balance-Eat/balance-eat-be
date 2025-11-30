package org.balanceeat.domain.reminder

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

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
                    sendDatetime = LocalDateTime.of(2025, 12, 2, 9, 0, 0)
                }
            )

            val command = reminderUpdateCommandFixture {
                id = reminder.id
                content = "수정 후 내용"
                sendDatetime = LocalDateTime.of(2025, 12, 2, 10, 0, 0)
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
}
