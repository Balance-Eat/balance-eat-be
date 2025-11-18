package org.balanceeat.domain.notification

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationDeviceWriter(
    private val notificationDeviceRepository: NotificationDeviceRepository
) {
    @Transactional
    fun create(command: NotificationDeviceCommand.Create): NotificationDeviceResult {
        validateCreation(command)

        val device = NotificationDevice(
            userId = command.userId,
            agentId = command.agentId,
            osType = command.osType,
            deviceName = command.deviceName,
            allowsNotification = command.allowsNotification
        )

        val savedDevice = notificationDeviceRepository.save(device)
        return NotificationDeviceResult.from(savedDevice)
    }

    private fun validateCreation(command: NotificationDeviceCommand.Create) {
        if (notificationDeviceRepository.existsByAgentId(command.agentId)) {
            throw DomainException(DomainStatus.NOTIFICATION_DEVICE_ALREADY_EXISTS)
        }
    }
}
