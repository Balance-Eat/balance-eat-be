package org.balanceeat.api.notification

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.balanceeat.api.config.supports.IntegrationTestContext
import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.notification.notificationDeviceFixture
import org.balanceeat.domain.notification.NotificationDeviceRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class NotificationDeviceServiceTest : IntegrationTestContext() {

    @Autowired
    private lateinit var notificationDeviceService: NotificationDeviceService

    @Autowired
    private lateinit var notificationDeviceRepository: NotificationDeviceRepository

    @Nested
    @DisplayName("알림 디바이스 등록")
    inner class CreateTest {

        @Test
        fun `알림 디바이스를 성공적으로 등록할 수 있다`() {
            // given
            val userId = 1L
            val request = notificationDeviceCreateV1RequestFixture {
                agentId = "new-agent-id"
                deviceName = "갤럭시 S24"
            }

            // when
            val result = notificationDeviceService.create(request, userId)

            // then
            assertThat(result.userId).isEqualTo(userId)
            assertThat(result.agentId).isEqualTo(request.agentId)
            assertThat(result.deviceName).isEqualTo(request.deviceName)
            assertThat(result.osType).isEqualTo(request.osType)
            assertThat(result.isActive).isEqualTo(request.isActive)
        }

        @Test
        fun `중복된 agentId로 등록하면 실패한다`() {
            // given
            val userId = 1L
            val existingDevice = createEntity(
                notificationDeviceFixture {
                    this.userId = userId
                    agentId = "duplicate-agent-id"
                }
            )

            val request = notificationDeviceCreateV1RequestFixture {
                agentId = "duplicate-agent-id"
            }

            // when
            val throwable = catchThrowable {
                notificationDeviceService.create(request, userId)
            }

            // then
            assertThat(throwable).isInstanceOf(DomainException::class.java)
                .hasFieldOrPropertyWithValue("status", DomainStatus.NOTIFICATION_DEVICE_ALREADY_EXISTS)
        }
    }

    @Nested
    @DisplayName("알림 활성화 상태 수정")
    inner class UpdateActivationTest {

        @Test
        fun `알림 활성화 상태를 성공적으로 수정할 수 있다`() {
            // given
            val userId = 1L
            val device = createEntity(
                notificationDeviceFixture {
                    this.userId = userId
                    isActive = true
                }
            )

            val request = notificationDeviceUpdateActivationV1RequestFixture {
                isActive = false
            }

            // when
            val result = notificationDeviceService.updateActivation(device.id, request, userId)

            // then
            assertThat(result.id).isEqualTo(device.id)
            assertThat(result.isActive).isFalse()
        }

        @Test
        fun `다른 사용자의 디바이스는 수정할 수 없다`() {
            // given
            val ownerId = 1L
            val otherUserId = 2L
            val device = createEntity(
                notificationDeviceFixture {
                    userId = ownerId
                    isActive = true
                }
            )

            val request = notificationDeviceUpdateActivationV1RequestFixture {
                isActive = false
            }

            // when
            val throwable = catchThrowable {
                notificationDeviceService.updateActivation(device.id, request, otherUserId)
            }

            // then
            assertThat(throwable).isInstanceOf(BadRequestException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.NOTIFICATION_DEVICE_UNAUTHORIZED)
        }

        @Test
        fun `존재하지 않는 디바이스는 수정할 수 없다`() {
            // given
            val request = notificationDeviceUpdateActivationV1RequestFixture {
                isActive = false
            }

            // when
            val throwable = catchThrowable {
                notificationDeviceService.updateActivation(999L, request, 1L)
            }

            // then
            assertThat(throwable).isInstanceOf(NotFoundException::class.java)
                .hasFieldOrPropertyWithValue("status", ApplicationStatus.NOTIFICATION_DEVICE_NOT_FOUND)
        }
    }
}
