package com.ymoch.study.server.record

import org.springframework.http.HttpStatus

data class ErrorRecord(
        val status: Int,
        val error: String,
        val message: String?) {

    constructor(
            status: Int,
            message: String? = null
    ) : this(
            status = status,
            error = HttpStatus.valueOf(status).reasonPhrase,
            message = message
    )
}
