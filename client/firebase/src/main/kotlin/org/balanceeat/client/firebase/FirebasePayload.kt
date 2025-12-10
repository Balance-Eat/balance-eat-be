package org.balanceeat.client.firebase

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification

class FirebaseRequest {
    data class SendMessage(
        val deviceToken: String,
        val title: String,
        val content: String,
        val deepLink: String? = null
    ) {
        fun toFirebaseMessage(): Message {
            return Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build()
                )
                .setToken(deviceToken)
                .putData("deepLink", deepLink ?: "")
                .build()
        }
    }

    data class SendBatchMessages(
        val deviceTokens: List<String>,
        val title: String,
        val content: String,
        val deepLink: String? = null
    ) {
        fun toFirebaseMessage(): MulticastMessage {
            return MulticastMessage.builder()
                .setNotification(
                    com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build()
                )
                .addAllTokens(deviceTokens)
                .putData("deepLink", deepLink ?: "")
                .build()
        }
    }
}