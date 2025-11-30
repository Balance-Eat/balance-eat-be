package org.balanceeat.client.firebase

class FirebaseRequest {
    data class SendMessage(
        val deviceToken: String,
        val title: String,
        val content: String,
        val deepLink: String
    )
}