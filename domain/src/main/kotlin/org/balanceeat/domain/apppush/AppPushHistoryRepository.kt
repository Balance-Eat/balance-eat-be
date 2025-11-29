package org.balanceeat.domain.apppush

import org.springframework.data.jpa.repository.JpaRepository

interface AppPushHistoryRepository : JpaRepository<AppPushHistory, Long> {
    fun findByUserId(userId: Long): List<AppPushHistory>
    fun findByDeviceId(deviceId: Long): List<AppPushHistory>
}
