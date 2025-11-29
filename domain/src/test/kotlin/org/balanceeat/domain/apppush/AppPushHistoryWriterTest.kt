package org.balanceeat.domain.apppush

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AppPushHistoryWriterTest : IntegrationTestContext() {
    @Autowired
    private lateinit var appPushHistoryWriter: AppPushHistoryWriter

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {
        @Test
        fun `앱 푸시 히스토리를 생성할 수 있다`() {
            // given
            val device = createEntity(notificationDeviceFixture())
            val command = appPushHistoryCreateCommandFixture { deviceId = device.id }

            // when
            val result = appPushHistoryWriter.create(command)

            // then
            assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(result)
        }
    }
}
