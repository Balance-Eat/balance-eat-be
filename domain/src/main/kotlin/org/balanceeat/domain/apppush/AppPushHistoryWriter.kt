package org.balanceeat.domain.apppush

import org.balanceeat.domain.common.DomainStatus.NOTIFICATION_DEVICE_NOT_FOUND
import org.balanceeat.domain.common.exception.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AppPushHistoryWriter(
    private val notificationDeviceRepository: NotificationDeviceRepository,
    private val appPushHistoryRepository: AppPushHistoryRepository
) {
    @Transactional
    fun create(command: AppPushHistoryCommand.Create): AppPushHistoryResult {
        val device = notificationDeviceRepository.findByIdOrNull(command.deviceId)
            ?: throw EntityNotFoundException(NOTIFICATION_DEVICE_NOT_FOUND)

        val appPushHistory = AppPushHistory(
            deviceId = device.id,
            userId = device.userId,
            agentId = device.agentId,
            title = command.title,
            content = command.content,
            deepLink = command.deepLink
        )

        val savedAppPushHistory = appPushHistoryRepository.save(appPushHistory)
        return AppPushHistoryResult.from(savedAppPushHistory)
    }
}
