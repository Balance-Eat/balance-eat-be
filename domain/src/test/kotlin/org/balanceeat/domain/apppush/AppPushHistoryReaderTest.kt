package org.balanceeat.domain.apppush

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AppPushHistoryReaderTest : IntegrationTestContext() {

    @Autowired
    private lateinit var appPushHistoryReader: AppPushHistoryReader

    @Autowired
    private lateinit var appPushHistoryRepository: AppPushHistoryRepository

    @Nested
    @DisplayName("사용자별 조회 테스트")
    inner class FindByUserIdTest {

        @Test
        fun `사용자 ID로 앱 푸시 히스토리 목록을 조회할 수 있다`() {
            // given
            val userId = 1L
            val history1 = appPushHistoryRepository.save(appPushHistoryFixture { this.userId = userId })
            val history2 = appPushHistoryRepository.save(appPushHistoryFixture { this.userId = userId })

            // when
            val results = appPushHistoryReader.findAllByUserId(userId)

            // then
            assertThat(results).hasSize(2)
            assertThat(results.map { it.id }).containsExactlyInAnyOrder(history1.id, history2.id)
        }
    }

    @Nested
    @DisplayName("디바이스별 조회 테스트")
    inner class FindByDeviceIdTest {

        @Test
        fun `디바이스 ID로 앱 푸시 히스토리 목록을 조회할 수 있다`() {
            // given
            val deviceId = 100L
            val history1 = appPushHistoryRepository.save(appPushHistoryFixture { this.deviceId = deviceId })
            val history2 = appPushHistoryRepository.save(appPushHistoryFixture { this.deviceId = deviceId })

            // when
            val results = appPushHistoryReader.findAllByDeviceId(deviceId)

            // then
            assertThat(results).hasSize(2)
            assertThat(results.map { it.id }).containsExactlyInAnyOrder(history1.id, history2.id)
        }
    }
}
