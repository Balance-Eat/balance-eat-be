package org.balanceeat.api.common.exception

import org.balanceeat.api.common.client.DiscordClient
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class ErrorNotificationSender(
    private val discordClient: DiscordClient,
    private val environment: Environment
) {
    fun send(message: String, e: Throwable) {
        val errorMessage = """
            message: %s
            stacktrace: %s
            """.trimIndent().format(
            message,
            e.toLimitedStackTrace()
        )
        send(errorMessage)
    }

    fun send(e: Throwable) {
        val errorMessage = """
            message: %s
            stacktrace: %s
            """.trimIndent().format(
            e.message,
            e.toLimitedStackTrace()
        )
        send(errorMessage)
    }

    fun send(message: String) {
        val sendableProfiles = setOf("dev", "prod")
        val activeProfiles = environment.activeProfiles.toSet()
        val shouldSend = activeProfiles.any { it in sendableProfiles }
        
        if (shouldSend) {
            discordClient.sendMessage(message)
        }
    }

    private fun Throwable.toLimitedStackTrace(maxLines: Int = 5): String {
        return this.stackTraceToString()
            .lineSequence()
            .take(maxLines)
            .joinToString("\n")
    }
}
