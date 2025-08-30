package org.balanceeat.domain.common.exception

import org.balanceeat.common.Status

class EntityNotFoundException(
    status: Status,
    message: String = status.message,
    isCritical: Boolean = false,
    cause: Throwable? = null
) : DomainException(status, message, isCritical, cause)