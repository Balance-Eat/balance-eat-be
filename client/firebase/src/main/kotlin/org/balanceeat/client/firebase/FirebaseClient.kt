package org.balanceeat.client.firebase

import com.balanceeat.client.exception.ClientBadRequestException
import com.balanceeat.client.exception.ClientServerErrorException
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FirebaseClient(
    private val firebaseMessaging: FirebaseMessaging
) {
    private val log = LoggerFactory.getLogger(FirebaseClient::class.java)

    fun sendMessage(request: FirebaseRequest.SendMessage) {
        try {
            firebaseMessaging.send(request.toFirebaseMessage())
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

    fun sendBatchMessages(request: FirebaseRequest.SendBatchMessages) {
        try {
            firebaseMessaging.sendEachForMulticast(request.toFirebaseMessage())
        } catch (fme: FirebaseMessagingException) {
            if (fme.httpResponse.statusCode in 400..499) {
                log.warn("Firebase batch message client error: ${fme.messagingErrorCode}, ${fme.errorCode}, ${fme.httpResponse.statusCode}, ${fme.message}", fme)
                throw ClientBadRequestException(FirebaseClientStatus.FIREBASE_ERROR, cause = fme)
            } else {
                log.error("Firebase batch message server error: ${fme.messagingErrorCode}, ${fme.errorCode}, ${fme.httpResponse.statusCode}, ${fme.message}", fme)
                throw ClientServerErrorException(FirebaseClientStatus.FIREBASE_ERROR, cause = fme)
            }
        } catch (e: Exception) {
            log.error("Unexpected Firebase batch message error: ${e.message}", e)
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

    fun createBatchMessage(request: FirebaseRequest.SendBatchMessages): MulticastMessage {
        val notification = Notification.builder()
            .setTitle(request.title)
            .setBody(request.content)
            .build()

        return MulticastMessage.builder()
            .setNotification(notification)
            .addAllTokens(request.deviceTokens)
            .putData("deepLink", request.deepLink ?: "")
            .build()
    }
}