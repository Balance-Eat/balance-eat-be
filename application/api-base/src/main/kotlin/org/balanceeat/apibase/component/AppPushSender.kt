package org.balanceeat.apibase.component

import org.balanceeat.client.firebase.FirebaseClient
import org.balanceeat.client.firebase.FirebaseRequest
import org.springframework.stereotype.Component

@Component
class AppPushSender(
    private val firebaseClient: FirebaseClient,
) {
    fun sendPush(
        agentId: String,
        title: String,
        content: String,
        deepLink: String
    ) {
        firebaseClient.sendMessage(
            FirebaseRequest.SendMessage(
                deviceToken = agentId,
                title = title,
                content = content,
                deepLink = deepLink
            )
        )
    }
}