package org.balanceeat.api.notification

import org.balanceeat.domain.notification.NotificationDeviceCommand
import org.balanceeat.domain.notification.NotificationDeviceWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationDeviceService(
    private val notificationDeviceWriter: NotificationDeviceWriter
) {
    @Transactional
    fun create(request: NotificationDeviceV1Request.Create, userId: Long): NotificationDeviceV1Response.Details {
        val result = notificationDeviceWriter.create(
            command = NotificationDeviceCommand.Create(
                userId = userId,
                agentId = request.agentId,
                osType = request.osType,
                deviceName = request.deviceName,
                allowsNotification = request.allowsNotification
            )
        )

        return NotificationDeviceV1Response.Details.from(result)
    }
}
