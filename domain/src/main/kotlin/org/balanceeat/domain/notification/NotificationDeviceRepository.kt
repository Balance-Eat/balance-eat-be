package org.balanceeat.domain.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationDeviceRepository : JpaRepository<NotificationDevice, Long> {
    fun existsByAgentId(agentId: String): Boolean
    fun findByUserId(userId: Long): List<NotificationDevice>
}
