package org.balanceeat.domain.apppush

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NotificationDeviceRepository : JpaRepository<NotificationDevice, Long> {
    fun existsByAgentId(agentId: String): Boolean

    fun findAllByUserId(userId: Long): List<NotificationDevice>

    @Query("SELECT nd FROM NotificationDevice nd WHERE nd.userId IN :userIds AND nd.isActive = true")
    fun findAllActiveByUserIdIn(userIds: List<Long>): List<NotificationDevice>

    fun findByUserIdAndAgentId(userId: Long, agentId: String): NotificationDevice?
}
