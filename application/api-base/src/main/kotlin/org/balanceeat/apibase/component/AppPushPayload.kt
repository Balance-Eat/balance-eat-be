package org.balanceeat.apibase.component

class AppPushRequest {
    data class SendMessage(
        val deviceId: Long,
        val title: String,
        val content: String,
        val deepLink: String? = null
    )
}