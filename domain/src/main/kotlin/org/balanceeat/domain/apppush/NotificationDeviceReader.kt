package org.balanceeat.domain.apppush

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class NotificationDeviceReader(
    private val notificationDeviceRepository: NotificationDeviceRepository
) : BaseReader<NotificationDevice, NotificationDeviceResult>(notificationDeviceRepository, NotificationDeviceResult) {

    override fun findByIdOrThrow(id: Long): NotificationDeviceResult {
        return findByIdOrThrow(id, DomainStatus.NOTIFICATION_DEVICE_NOT_FOUND)
    }

    fun findByUserId(userId: Long): List<NotificationDeviceResult> {
        return notificationDeviceRepository.findByUserId(userId)
            .map { NotificationDeviceResult.from(it) }
    }

    fun existsByAgentId(agentId: String): Boolean {
        return notificationDeviceRepository.existsByAgentId(agentId)
    }

    fun findByUserIdAndAgentId(userId: Long, agentId: String): NotificationDeviceResult? {
        return notificationDeviceRepository.findByUserIdAndAgentId(userId, agentId)
            ?.let { NotificationDeviceResult.from(it) }
    }
}
