package org.balanceeat.domain.apppush

import org.balanceeat.domain.common.EntityMapper
import java.time.LocalDateTime

data class AppPushHistoryResult(
    val id: Long,
    val deviceId: Long,
    val userId: Long,
    val agentId: String,
    val title: String,
    val content: String,
    val deepLink: String?,
    val createdAt: LocalDateTime
) {
    companion object : EntityMapper<AppPushHistory, AppPushHistoryResult> {
        override fun from(entity: AppPushHistory): AppPushHistoryResult {
            return AppPushHistoryResult(
                id = entity.id,
                deviceId = entity.deviceId,
                userId = entity.userId,
                agentId = entity.agentId,
                title = entity.title,
                content = entity.content,
                deepLink = entity.deepLink,
                createdAt = entity.createdAt
            )
        }
    }
}
