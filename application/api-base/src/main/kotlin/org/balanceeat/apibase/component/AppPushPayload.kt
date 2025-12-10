package org.balanceeat.apibase.component

class AppPushRequest {
    data class SendMessage(
        val deviceId: Long,
        val title: String,
        val content: String,
        val deepLink: String? = null
    )

    data class SendBatchMessages(
        val deviceIds: List<Long>,
        val title: String,
        val content: String,
        val deepLink: String? = null
    )
}