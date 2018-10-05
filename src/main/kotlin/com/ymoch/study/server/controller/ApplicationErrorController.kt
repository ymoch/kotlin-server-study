package com.ymoch.study.server.controller

import com.ymoch.study.server.record.ErrorRecord
import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.ErrorService
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(path = ["error"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class ApplicationErrorController(
        private val errorService: ErrorService
) : ErrorController {

    override fun getErrorPath(): String = "/error"

    @GetMapping
    fun showError(request: WebRequest, response: HttpServletResponse): ErrorRecord {
        val record = errorService.createRecord(request)
        response.status = record.status
        return record
    }

    @GetMapping(path = ["raise"])
    fun raiseError() {
        throw ApplicationRuntimeException("An expected error occurred.", status = 400)
    }

    @GetMapping(path = ["raise/unexpected"])
    fun raiseErrorUnexpected() {
        throw RuntimeException("An unexpected error occurred.")
    }
}
