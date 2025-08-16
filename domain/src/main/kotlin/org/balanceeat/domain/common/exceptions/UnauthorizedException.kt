package org.balanceeat.domain.common.exceptions

import org.balanceeat.domain.common.ApplicationException
import org.balanceeat.domain.common.Status

class UnauthorizedException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : ApplicationException(status, message, cause)