package org.balanceeat.api.notification

import org.balanceeat.apibase.ApplicationStatus
import org.balanceeat.apibase.exception.BadRequestException
import org.balanceeat.apibase.exception.NotFoundException
import org.balanceeat.domain.notification.NotificationDeviceCommand
import org.balanceeat.domain.notification.NotificationDeviceReader
import org.balanceeat.domain.notification.NotificationDeviceWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationDeviceService(
    private val notificationDeviceWriter: NotificationDeviceWriter,
    private val notificationDeviceReader: NotificationDeviceReader
) {
    @Transactional
    fun create(request: NotificationDeviceV1Request.Create, userId: Long): NotificationDeviceV1Response.Details {
        val result = notificationDeviceWriter.create(
            command = NotificationDeviceCommand.Create(
                userId = userId,
                agentId = request.agentId,
                osType = request.osType,
                deviceName = request.deviceName,
                isActive = request.isActive
            )
        )

        return NotificationDeviceV1Response.Details.from(result)
    }

    @Transactional
    fun updateActivation(
        deviceId: Long,
        request: NotificationDeviceV1Request.UpdateActivation,
        userId: Long
    ): NotificationDeviceV1Response.Details {
        val device = notificationDeviceReader.findById(deviceId)
            ?: throw NotFoundException(ApplicationStatus.NOTIFICATION_DEVICE_NOT_FOUND)

        if (device.userId != userId) {
            throw BadRequestException(ApplicationStatus.NOTIFICATION_DEVICE_UNAUTHORIZED)
        }

        val result = notificationDeviceWriter.update(
            command = NotificationDeviceCommand.Update(
                id = deviceId,
                isActive = request.isActive
            )
        )

        return NotificationDeviceV1Response.Details.from(result)
    }

    @Transactional(readOnly = true)
    fun getCurrent(userId: Long, agentId: String): NotificationDeviceV1Response.Details {
        val device = notificationDeviceReader.findByUserIdAndAgentId(userId, agentId)
            ?: throw NotFoundException(ApplicationStatus.NOTIFICATION_DEVICE_NOT_FOUND)

        return NotificationDeviceV1Response.Details.from(device)
    }
}
