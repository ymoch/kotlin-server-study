package com.ymoch.study.server.controller

import com.ymoch.study.server.exception.ApplicationRuntimeException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(path = ["error"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class ApplicationErrorController : ErrorController {

    @Autowired
    lateinit var errorAttributes: ErrorAttributes

    @Value("\${debug:false}")
    var debug: Boolean = false

    override fun getErrorPath(): String = "/error"

    @GetMapping
    fun showError(request: WebRequest, response: HttpServletResponse): Map<String, Any?> {
        val error: Throwable? = errorAttributes.getError(request)
        val attributes = errorAttributes.getErrorAttributes(request, debug)
        val cause = decideCause(error, attributes)

        response.status = cause.status
        return mapOf(
                "timestamp" to attributes["timestamp"],
                "status" to cause.status,
                "error" to HttpStatus.valueOf(cause.status).reasonPhrase,
                "message" to cause.message
        )
    }

    @GetMapping(path = ["raise"])
    fun raiseError() {
        throw ApplicationRuntimeException("An expected error occurred.", status = 400)
    }

    @GetMapping(path = ["raise/unexpected"])
    fun raiseErrorUnexpected() {
        throw RuntimeException("An Unexpected error occurred.")
    }
}

fun decideCause(error: Throwable?, requestAttributes: Map<String, Any>): ApplicationRuntimeException {
    if (error == null) {
        return ApplicationRuntimeException(
                message = requestAttributes["error"] as? String ?: "No messages.",
                status = Integer.valueOf(requestAttributes["status"] as? String ?: "500"))
    }
    if (error !is ApplicationRuntimeException) {
        return ApplicationRuntimeException(
                message = error.message ?: "Unexpected error.",
                cause = error,
                status = 500)
    }
    return error
}
