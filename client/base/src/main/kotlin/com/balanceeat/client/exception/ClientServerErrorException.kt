package com.balanceeat.client.exception

import org.balanceeat.common.Status

class ClientServerErrorException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null,
    isCritical: Boolean = true
) : ClientException(status, message, cause, isCritical)