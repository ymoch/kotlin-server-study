package com.ymoch.study.server.entity

import org.springframework.http.HttpStatus

class ErrorRecord(val status: Int, val error: String, val message: String? = null) {

    constructor(status: Int, message: String? = null) : this(
            status = status,
            error = HttpStatus.valueOf(status).reasonPhrase,
            message = message
    )
}
