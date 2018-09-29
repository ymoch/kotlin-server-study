package com.ymoch.study.server.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["hello"])
class HelloController {

    @GetMapping(path = ["world"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun helloWorld() = "Hello, world."
}
