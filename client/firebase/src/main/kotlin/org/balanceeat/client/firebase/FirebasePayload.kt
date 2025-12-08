package org.balanceeat.client.firebase

class FirebaseRequest {
    data class SendMessage(
        val deviceToken: String,
        val title: String,
        val content: String,
        val deepLink: String? = null
    )

    data class SendMulticastMessage(
        val deviceTokens: List<String>,
        val title: String,
        val content: String,
        val deepLink: String? = null
    )
}