package com.balanceeat.client.exception

import org.balanceeat.common.Status

class ClientBadRequestException(
    status: Status,
    message: String = status.message,
    cause: Throwable? = null
) : ClientException(status, message, cause)