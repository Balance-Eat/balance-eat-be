package org.balanceeat.domain.notification

import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDateTime

data class NotificationDeviceResult(
    val id: Long,
    val userId: Long,
    val agentId: String,
    val osType: NotificationDevice.OsType,
    val deviceName: String,
    val allowsNotification: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object : EntityMapper<NotificationDevice, NotificationDeviceResult> {
        override fun from(entity: NotificationDevice): NotificationDeviceResult {
            return NotificationDeviceResult(
                id = entity.id,
                userId = entity.userId,
                agentId = entity.agentId,
                osType = entity.osType,
                deviceName = entity.deviceName,
                allowsNotification = entity.allowsNotification,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
