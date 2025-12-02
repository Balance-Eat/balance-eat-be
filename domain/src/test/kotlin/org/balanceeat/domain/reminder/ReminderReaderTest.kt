package org.balanceeat.domain.reminder

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

class ReminderReaderTest : IntegrationTestContext() {

    @Autowired
    private lateinit var reminderReader: ReminderReader

    @Autowired
    private lateinit var reminderRepository: ReminderRepository

    @Nested
    @DisplayName("사용자 ID로 전체 조회 테스트")
    inner class FindAllByUserIdTest {

        @Test
        fun `사용자 ID로 리마인더 목록을 페이지네이션하여 조회할 수 있다`() {
            // given
            val userId = 1L
            val reminder1 = reminderRepository.save(reminderFixture { this.userId = userId })
            val reminder2 = reminderRepository.save(reminderFixture { this.userId = userId })
            reminderRepository.save(reminderFixture { this.userId = 2L }) // 다른 사용자의 리마인더

            val pageable = PageRequest.of(0, 10)

            // when
            val results = reminderReader.findAllByUserId(userId, pageable)

            // then
            assertThat(results.content).hasSize(2)
            assertThat(results.content.map { it.id }).containsExactlyInAnyOrder(reminder1.id, reminder2.id)
            assertThat(results.totalPages).isEqualTo(1)
            assertThat(results.totalElements).isEqualTo(2)
        }

        @Test
        fun `사용자에게 리마인더가 없으면 빈 페이지를 반환한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 10)

            // when
            val results = reminderReader.findAllByUserId(userId, pageable)

            // then
            assertThat(results.content).isEmpty()
            assertThat(results.totalElements).isEqualTo(0)
        }
    }
}
