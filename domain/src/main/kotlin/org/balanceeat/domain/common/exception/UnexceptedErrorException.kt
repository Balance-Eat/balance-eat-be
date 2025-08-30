package org.balanceeat.domain.common.exception

import org.balanceeat.common.Status

class UnexceptedErrorException(
    status: Status,
    message: String = status.message,
    isCritical: Boolean = true,
    cause: Throwable? = null
) : DomainException(status, message, isCritical, cause)