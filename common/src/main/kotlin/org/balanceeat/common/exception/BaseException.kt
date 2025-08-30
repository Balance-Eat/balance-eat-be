package org.balanceeat.common.exception

import org.balanceeat.common.Status

abstract class BaseException (
    val status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : RuntimeException(message, cause)