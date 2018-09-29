package com.ymoch.study.server.service.impl

import com.ymoch.study.server.entity.ErrorRecord
import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.ErrorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.context.request.WebRequest

val DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR

@Component
@Service
class ErrorServiceImpl(private val errorAttributes: ErrorAttributes) : ErrorService {

    @Value("\${debug:false}")
    private var debug = false

    override fun createEntity(request: WebRequest): ErrorRecord {
        val error: Throwable? = errorAttributes.getError(request)
        val attributes = errorAttributes.getErrorAttributes(request, debug)

        if (error == null) {
            val status = HttpStatus.resolve(attributes["status"] as Int) ?: DEFAULT_STATUS;
            val message = attributes["error"] as? String
            return ErrorRecord(status.value(), message)
        }

        if (error !is ApplicationRuntimeException) {
            val message = error.message ?: "Unexpected error."
            return ErrorRecord(DEFAULT_STATUS.value(), message)
        }

        return ErrorRecord(error.status, error.message)
    }
}