package com.ymoch.study.server.controller

import com.ymoch.study.server.record.greeting.GreetingRecord
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["hello"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class HelloController {

    @GetMapping(path = ["world"])
    fun sayHello(
            @RequestParam(required = false) target: String?
    ): GreetingRecord {
        val finalTarget = target ?: "world"
        val message = "Hello, $finalTarget."
        return GreetingRecord(message = message, target = finalTarget)
    }
}
