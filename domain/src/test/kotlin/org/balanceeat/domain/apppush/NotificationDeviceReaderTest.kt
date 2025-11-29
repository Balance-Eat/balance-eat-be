package org.balanceeat.domain.apppush

import org.assertj.core.api.Assertions.assertThat
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class NotificationDeviceReaderTest : IntegrationTestContext() {

    @Autowired
    private lateinit var notificationDeviceReader: NotificationDeviceReader

    @Autowired
    private lateinit var notificationDeviceRepository: NotificationDeviceRepository

    // BaseReader의 기본 메서드(findById, findByIdOrThrow 등)는 테스트 작성 불필요
    // 아래는 도메인별 커스텀 조회 메서드만 테스트

    @Nested
    @DisplayName("사용자별 조회 테스트")
    inner class FindByUserIdTest {

        @Test
        fun `사용자 ID로 알림 디바이스 목록을 조회할 수 있다`() {
            // given
            val userId = 1L
            val device1 = notificationDeviceRepository.save(notificationDeviceFixture { this.userId = userId })
            val device2 = notificationDeviceRepository.save(notificationDeviceFixture { this.userId = userId })

            // when
            val results = notificationDeviceReader.findByUserId(userId)

            // then
            assertThat(results).hasSize(2)
            assertThat(results.map { it.id }).containsExactlyInAnyOrder(device1.id, device2.id)
        }
    }

    @Nested
    @DisplayName("존재 여부 확인 테스트")
    inner class ExistsByAgentIdTest {

        @Test
        fun `agentId로 존재 여부를 확인할 수 있다`() {
            // given
            val agentId = "test-agent-id"
            notificationDeviceRepository.save(notificationDeviceFixture { this.agentId = agentId })

            // when
            val exists = notificationDeviceReader.existsByAgentId(agentId)

            // then
            assertThat(exists).isTrue()
        }

        @Test
        fun `존재하지 않는 경우 false를 반환한다`() {
            // when
            val exists = notificationDeviceReader.existsByAgentId("non-existent-agent-id")

            // then
            assertThat(exists).isFalse()
        }
    }
}
