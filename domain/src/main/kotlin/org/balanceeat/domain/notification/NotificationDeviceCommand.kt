package org.balanceeat.domain.notification

class NotificationDeviceCommand {

    data class Create(
        val userId: Long,
        val agentId: String,
        val osType: NotificationDevice.OsType,
        val deviceName: String,
        val allowsNotification: Boolean
    )
}
