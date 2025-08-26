package org.balanceeat.api.common.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class DiscordClient(
    private val restClient: RestClient,
    @Value("\${application.discord.webhook.url}")
    private val webhookUrl: String
) {
    fun sendMessage(message: String) {
        val payload = mapOf("content" to message)
        restClient.post()
            .uri(webhookUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .retrieve()
            .toBodilessEntity()
    }
}
