package org.balanceeat.domain.common.exception

import org.balanceeat.common.Status

class BadCommandException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : DomainException(status, message, false, cause)