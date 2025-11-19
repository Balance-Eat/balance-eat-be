package org.balanceeat.api.notification

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.notification.NotificationDevice
import java.util.UUID

class NotificationDeviceV1RequestFixture {
    data class Create(
        var agentId: String = UUID.randomUUID().toString(),
        var osType: NotificationDevice.OsType = NotificationDevice.OsType.AOS,
        var deviceName: String = "테스트 디바이스",
        var isActive: Boolean = true
    ) : TestFixture<NotificationDeviceV1Request.Create> {
        override fun create(): NotificationDeviceV1Request.Create {
            return NotificationDeviceV1Request.Create(
                agentId = agentId,
                osType = osType,
                deviceName = deviceName,
                isActive = isActive
            )
        }
    }

    data class UpdateActivation(
        var isActive: Boolean = true
    ) : TestFixture<NotificationDeviceV1Request.UpdateActivation> {
        override fun create(): NotificationDeviceV1Request.UpdateActivation {
            return NotificationDeviceV1Request.UpdateActivation(
                isActive = isActive
            )
        }
    }
}

fun notificationDeviceCreateV1RequestFixture(block: NotificationDeviceV1RequestFixture.Create.() -> Unit = {}): NotificationDeviceV1Request.Create {
    return NotificationDeviceV1RequestFixture.Create().copy().apply(block).create()
}

fun notificationDeviceUpdateActivationV1RequestFixture(block: NotificationDeviceV1RequestFixture.UpdateActivation.() -> Unit = {}): NotificationDeviceV1Request.UpdateActivation {
    return NotificationDeviceV1RequestFixture.UpdateActivation().copy().apply(block).create()
}
