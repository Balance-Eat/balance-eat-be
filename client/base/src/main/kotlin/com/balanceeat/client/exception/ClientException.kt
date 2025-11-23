package com.balanceeat.client.exception

import org.balanceeat.common.Status
import org.balanceeat.common.exception.BaseException

abstract class ClientException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null,
    val isCritical: Boolean = false
) : BaseException(status, message, cause)