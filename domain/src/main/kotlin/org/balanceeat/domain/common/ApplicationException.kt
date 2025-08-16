package org.balanceeat.domain.common

abstract class ApplicationException(
    val status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    
    fun status(): Status = status
    fun message(): String = message ?: ""
}