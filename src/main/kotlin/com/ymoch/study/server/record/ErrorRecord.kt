package com.ymoch.study.server.record

import org.springframework.http.HttpStatus

class ErrorRecord(
        val status: Int,
        val error: String,
        val message: String?) {

    constructor(
            status: Int,
            message: String? = null,
            name: String? = null,
            stackTrace: List<String>? = null
    ) : this(
            status = status,
            error = HttpStatus.valueOf(status).reasonPhrase,
            message = message
    )
}
