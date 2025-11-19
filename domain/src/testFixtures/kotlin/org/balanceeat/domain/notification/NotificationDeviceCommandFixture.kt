package org.balanceeat.domain.notification

import org.balanceeat.common.TestFixture
import java.util.UUID

class NotificationDeviceCommandFixture {

    class Create(
        var userId: Long = 1L,
        var agentId: String = UUID.randomUUID().toString(),
        var osType: NotificationDevice.OsType = NotificationDevice.OsType.AOS,
        var deviceName: String = "테스트 디바이스",
        var isActive: Boolean = true
    ) : TestFixture<NotificationDeviceCommand.Create> {
        override fun create(): NotificationDeviceCommand.Create {
            return NotificationDeviceCommand.Create(
                userId = userId,
                agentId = agentId,
                osType = osType,
                deviceName = deviceName,
                isActive = isActive
            )
        }
    }

    class Update(
        var id: Long = 1L,
        var isActive: Boolean = true
    ) : TestFixture<NotificationDeviceCommand.Update> {
        override fun create(): NotificationDeviceCommand.Update {
            return NotificationDeviceCommand.Update(
                id = id,
                isActive = isActive
            )
        }
    }
}

fun notificationDeviceCreateCommandFixture(block: NotificationDeviceCommandFixture.Create.() -> Unit = {}): NotificationDeviceCommand.Create {
    return NotificationDeviceCommandFixture.Create().apply(block).create()
}

fun notificationDeviceUpdateCommandFixture(block: NotificationDeviceCommandFixture.Update.() -> Unit = {}): NotificationDeviceCommand.Update {
    return NotificationDeviceCommandFixture.Update().apply(block).create()
}
