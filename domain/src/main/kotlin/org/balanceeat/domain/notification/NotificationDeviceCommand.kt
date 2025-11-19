package org.balanceeat.domain.notification

class NotificationDeviceCommand {

    data class Create(
        val userId: Long,
        val agentId: String,
        val osType: NotificationDevice.OsType,
        val deviceName: String,
        val isActive: Boolean
    )

    data class Update(
        val id: Long,
        val isActive: Boolean
    )
}
