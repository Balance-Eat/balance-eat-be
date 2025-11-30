package org.balanceeat.client.firebase

import com.balanceeat.client.exception.ClientBadRequestException
import com.balanceeat.client.exception.ClientServerErrorException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FirebaseClient(
    private val firebaseMessaging: FirebaseMessaging
) {
    private val log = LoggerFactory.getLogger(FirebaseClient::class.java)

    fun sendMessage(request: FirebaseRequest.SendMessage) {
        val message = createMessage(request)

        try {
            firebaseMessaging.send(message)
        } catch (fme: FirebaseMessagingException) {
            if (fme.httpResponse.statusCode in 400..499) {
                log.warn("Firebase client error: ${fme.messagingErrorCode}, ${fme.errorCode}, ${fme.httpResponse.statusCode}, ${fme.message}", fme)
                throw ClientBadRequestException(FirebaseClientStatus.FIREBASE_ERROR, cause = fme)
            } else {
                log.error("Firebase server error: ${fme.messagingErrorCode}, ${fme.errorCode}, ${fme.httpResponse.statusCode}, ${fme.message}", fme)
                throw ClientServerErrorException(FirebaseClientStatus.FIREBASE_ERROR, cause = fme)
            }
        } catch (e: Exception) {
            log.error("Unexpected Firebase error: ${e.message}", e)
            throw ClientServerErrorException(FirebaseClientStatus.FIREBASE_ERROR, cause = e)
        }
    }

    fun createMessage(request: FirebaseRequest.SendMessage): Message {
        val notification = Notification.builder()
            .setTitle(request.title)
            .setBody(request.content)
            .build()

        return Message.builder()
            .setNotification(notification)
            .setToken(request.deviceToken)
            .putData("deepLink", request.deepLink)
            .build()
    }
}