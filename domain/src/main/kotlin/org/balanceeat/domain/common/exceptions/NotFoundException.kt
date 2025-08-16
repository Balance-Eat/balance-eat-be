package org.balanceeat.domain.common.exceptions

import org.balanceeat.domain.common.ApplicationException
import org.balanceeat.domain.common.Status

class NotFoundException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : ApplicationException(status, message, cause)