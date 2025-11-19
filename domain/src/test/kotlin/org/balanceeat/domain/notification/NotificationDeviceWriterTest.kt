package org.balanceeat.domain.notification

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.config.supports.IntegrationTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class NotificationDeviceWriterTest : IntegrationTestContext() {

    @Autowired
    private lateinit var notificationDeviceWriter: NotificationDeviceWriter

    @Autowired
    private lateinit var notificationDeviceRepository: NotificationDeviceRepository

    @Nested
    @DisplayName("생성 테스트")
    inner class CreateTest {

        @Test
        fun `알림 디바이스를 생성할 수 있다`() {
            // given
            val command = notificationDeviceCreateCommandFixture()

            // when
            val result = notificationDeviceWriter.create(command)

            // then
            assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(command)
        }

        @Test
        fun `중복된 agentId로 알림 디바이스를 생성하면 예외가 발생한다`() {
            // given
            val existingDevice = notificationDeviceRepository.save(
                notificationDeviceFixture {
                    agentId = "duplicate-agent-id"
                }
            )

            val command = notificationDeviceCreateCommandFixture {
                agentId = "duplicate-agent-id"
            }

            // when & then
            assertThatThrownBy { notificationDeviceWriter.create(command) }
                .isInstanceOf(DomainException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.NOTIFICATION_DEVICE_ALREADY_EXISTS)
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    inner class UpdateTest {

        @Test
        fun `알림 디바이스를 수정할 수 있다`() {
            // given
            val device = notificationDeviceRepository.save(
                notificationDeviceFixture {
                    isActive = true
                }
            )

            val command = notificationDeviceUpdateCommandFixture {
                id = device.id
                isActive = false
            }

            // when
            val result = notificationDeviceWriter.update(command)

            // then
            assertThat(result.isActive).isFalse()
            assertThat(result.id).isEqualTo(device.id)
        }
    }
}
