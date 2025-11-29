package org.balanceeat.domain.apppush

class AppPushHistoryCommand {

    data class Create(
        val deviceId: Long,
        val title: String,
        val content: String,
        val deepLink: String? = null
    )
}
