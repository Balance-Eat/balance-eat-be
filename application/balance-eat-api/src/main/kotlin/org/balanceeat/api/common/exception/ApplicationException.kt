package org.balanceeat.api.common.exception

import org.balanceeat.common.Status
import org.balanceeat.common.exception.BaseException

abstract class ApplicationException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : BaseException(status, message, cause)