package org.balanceeat.domain.apppush

import org.balanceeat.common.TestFixture
import org.balanceeat.domain.config.NEW_ID
import java.util.UUID

class AppPushHistoryFixture(
    var id: Long = NEW_ID,
    var deviceId: Long = 1L,
    var userId: Long = 1L,
    var agentId: String = UUID.randomUUID().toString(),
    var title: String = "테스트 푸시 제목",
    var content: String = "테스트 푸시 내용입니다.",
    var deepLink: String? = null
) : TestFixture<AppPushHistory> {
    override fun create(): AppPushHistory {
        return AppPushHistory(
            id = id,
            deviceId = deviceId,
            userId = userId,
            agentId = agentId,
            title = title,
            content = content,
            deepLink = deepLink
        )
    }
}

fun appPushHistoryFixture(block: AppPushHistoryFixture.() -> Unit = {}): AppPushHistory {
    return AppPushHistoryFixture().apply(block).create()
}
