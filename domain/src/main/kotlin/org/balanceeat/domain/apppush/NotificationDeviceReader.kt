package org.balanceeat.domain.apppush

import org.balanceeat.domain.common.BaseReader
import org.balanceeat.domain.common.DomainStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class NotificationDeviceReader(
    private val notificationDeviceRepository: NotificationDeviceRepository,
) : BaseReader<NotificationDevice, NotificationDeviceResult>(notificationDeviceRepository, NotificationDeviceResult) {
    override fun findByIdOrThrow(id: Long): NotificationDeviceResult = findByIdOrThrow(id, DomainStatus.NOTIFICATION_DEVICE_NOT_FOUND)

    fun findAllByUserId(userId: Long): List<NotificationDeviceResult> = notificationDeviceRepository
        .findAllByUserId(userId)
            .map { NotificationDeviceResult.from(it) }

    fun findAllActiveByUserIds(userIds: List<Long>): List<NotificationDeviceResult> = notificationDeviceRepository
        .findAllActiveByUserIdIn(userIds)
            .map { NotificationDeviceResult.from(it) }

    fun existsByAgentId(agentId: String): Boolean = notificationDeviceRepository.existsByAgentId(agentId)

    fun findByUserIdAndAgentId(
        userId: Long,
        agentId: String,
    ): NotificationDeviceResult? = notificationDeviceRepository
        .findByUserIdAndAgentId(userId, agentId)
            ?.let { NotificationDeviceResult.from(it) }
}
