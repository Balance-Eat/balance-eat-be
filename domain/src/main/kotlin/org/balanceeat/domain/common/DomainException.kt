package org.balanceeat.domain.common

class DomainException(
    val status: Status,
    message: String = status.message,
    val isCritical: Boolean = false,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
}