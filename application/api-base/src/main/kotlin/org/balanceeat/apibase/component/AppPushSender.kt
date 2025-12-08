package org.balanceeat.apibase.component

import org.balanceeat.client.firebase.FirebaseClient
import org.balanceeat.client.firebase.FirebaseRequest
import org.balanceeat.domain.apppush.AppPushHistoryCommand
import org.balanceeat.domain.apppush.AppPushHistoryWriter
import org.balanceeat.domain.apppush.NotificationDeviceReader
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AppPushSender(
    private val firebaseClient: FirebaseClient,
    private val appPushHistoryWriter: AppPushHistoryWriter,
    private val notificationDeviceReader: NotificationDeviceReader
) {
    @Async
    @Transactional
    fun sendPushAsync(request: AppPushRequest.SendMessage) {
        val (deviceId, title, content, deepLink) = request
        val device = notificationDeviceReader.findByIdOrThrow(deviceId)

        firebaseClient.sendMessage(
            FirebaseRequest.SendMessage(
                deviceToken = device.agentId,
                title = title,
                content = content,
                deepLink = deepLink
            )
        )

        appPushHistoryWriter.create(
            AppPushHistoryCommand.Create(
                deviceId = deviceId,
                title = title,
                content = content,
                deepLink = deepLink
            )
        )
    }
}