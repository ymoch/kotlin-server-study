package com.ymoch.study.server.controller

import com.ymoch.study.server.exception.ApplicationRuntimeException
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["error"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class ErrorController {

    @GetMapping(path = ["raise"])
    fun raiseError() {
        throw ApplicationRuntimeException("An expected error occurred.", status = 400)
    }

    @GetMapping(path = ["raise/unexpected"])
    fun raiseErrorUnexpected() {
        throw RuntimeException("An unexpected error occurred.")
    }
}
