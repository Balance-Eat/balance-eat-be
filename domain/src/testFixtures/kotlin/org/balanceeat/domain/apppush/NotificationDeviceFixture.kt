package org.balanceeat.domain.apppush

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.config.NEW_ID
import java.util.UUID

class NotificationDeviceFixture(
    var id: Long = NEW_ID,
    var userId: Long = 1L,
    var agentId: String = UUID.randomUUID().toString(),
    var osType: NotificationDevice.OsType = NotificationDevice.OsType.AOS,
    var deviceName: String = "테스트 디바이스",
    var isActive: Boolean = true
) : TestFixture<NotificationDevice> {
    override fun create(): NotificationDevice {
        return NotificationDevice(
            id = id,
            userId = userId,
            agentId = agentId,
            osType = osType,
            deviceName = deviceName,
            isActive = isActive
        )
    }
}

fun notificationDeviceFixture(block: NotificationDeviceFixture.() -> Unit = {}): NotificationDevice {
    return NotificationDeviceFixture().apply(block).create()
}
