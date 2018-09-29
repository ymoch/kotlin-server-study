package com.ymoch.study.server.exception

class ApplicationRuntimeException(
        message: String? = null,
        cause: Throwable? = null,
        val status: Int = 500
) : RuntimeException(message, cause)
