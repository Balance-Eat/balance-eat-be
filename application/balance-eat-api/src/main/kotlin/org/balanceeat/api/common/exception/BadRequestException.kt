package org.balanceeat.api.common.exception

import org.balanceeat.api.common.exception.ApplicationException
import org.balanceeat.common.Status

class BadRequestException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : ApplicationException(status, message, cause)