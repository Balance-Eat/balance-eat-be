package org.balanceeat.apibase.exception

import org.balanceeat.common.Status

class BadRequestException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : ApplicationException(status, message, cause)