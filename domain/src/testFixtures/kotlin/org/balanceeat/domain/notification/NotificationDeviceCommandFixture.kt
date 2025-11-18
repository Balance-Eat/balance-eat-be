package org.balanceeat.domain.notification

import org.balanceeat.common.TestFixture
import java.util.UUID

class NotificationDeviceCommandFixture {

    class Create(
        var userId: Long = 1L,
        var agentId: String = UUID.randomUUID().toString(),
        var osType: NotificationDevice.OsType = NotificationDevice.OsType.AOS,
        var deviceName: String = "테스트 디바이스",
        var allowsNotification: Boolean = true
    ) : TestFixture<NotificationDeviceCommand.Create> {
        override fun create(): NotificationDeviceCommand.Create {
            return NotificationDeviceCommand.Create(
                userId = userId,
                agentId = agentId,
                osType = osType,
                deviceName = deviceName,
                allowsNotification = allowsNotification
            )
        }
    }
}

fun notificationDeviceCreateCommandFixture(block: NotificationDeviceCommandFixture.Create.() -> Unit = {}): NotificationDeviceCommand.Create {
    return NotificationDeviceCommandFixture.Create().apply(block).create()
}
