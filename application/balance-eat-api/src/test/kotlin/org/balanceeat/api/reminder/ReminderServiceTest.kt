package org.balanceeat.api.reminder

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ReminderServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var reminderService: ReminderService

    @Nested
    @DisplayName("리마인더 등록")
    inner class CreateTest {

        @Test
        fun `리마인더를 성공적으로 등록할 수 있다`() {
            // given
            val userId = 1L
            val request = reminderCreateV1RequestFixture {}

            // when
            val result = reminderService.create(request, userId)

            // then
            assertThat(request)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }
}