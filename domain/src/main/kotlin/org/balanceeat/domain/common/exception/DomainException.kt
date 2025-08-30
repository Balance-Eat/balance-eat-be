package org.balanceeat.domain.common.exception

import org.balanceeat.common.Status
import org.balanceeat.common.exception.BaseException

open class DomainException(
    status: Status,
    message: String = status.message,
    val isCritical: Boolean = false,
    cause: Throwable? = null
) : BaseException(status, message, cause)