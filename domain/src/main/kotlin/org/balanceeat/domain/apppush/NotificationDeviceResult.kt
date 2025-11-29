package org.balanceeat.domain.apppush

import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDateTime

data class NotificationDeviceResult(
    val id: Long,
    val userId: Long,
    val agentId: String,
    val osType: NotificationDevice.OsType,
    val deviceName: String,
    val isActive: Boolean,
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
                isActive = entity.isActive,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
