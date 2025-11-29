package org.balanceeat.domain.apppush

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationDeviceRepository : JpaRepository<NotificationDevice, Long> {
    fun existsByAgentId(agentId: String): Boolean
    fun findByUserId(userId: Long): List<NotificationDevice>
    fun findByUserIdAndAgentId(userId: Long, agentId: String): NotificationDevice?
}
