package org.balanceeat.domain.notification

import org.balanceeat.domain.common.DomainStatus
import org.balanceeat.domain.common.exception.DomainException
import org.balanceeat.domain.common.exception.EntityNotFoundException
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
            isActive = command.isActive
        )

        val savedDevice = notificationDeviceRepository.save(device)
        return NotificationDeviceResult.from(savedDevice)
    }

    @Transactional
    fun update(command: NotificationDeviceCommand.Update): NotificationDeviceResult {
        val device = notificationDeviceRepository.findById(command.id)
            .orElseThrow { EntityNotFoundException(DomainStatus.NOTIFICATION_DEVICE_NOT_FOUND) }

        device.update(command.isActive)

        val savedDevice = notificationDeviceRepository.save(device)
        return NotificationDeviceResult.from(savedDevice)
    }

    private fun validateCreation(command: NotificationDeviceCommand.Create) {
        if (notificationDeviceRepository.existsByAgentId(command.agentId)) {
            throw DomainException(DomainStatus.NOTIFICATION_DEVICE_ALREADY_EXISTS)
        }
    }
}
