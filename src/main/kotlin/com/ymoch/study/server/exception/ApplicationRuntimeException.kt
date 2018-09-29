package com.ymoch.study.server.exception

class ApplicationRuntimeException(
        message: String,
        cause: Throwable? = null,
        val status: Int = 500
) : RuntimeException(message, cause)
