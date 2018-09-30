package com.ymoch.study.server.service.impl

import com.ymoch.study.server.entity.ErrorRecord
import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.ErrorService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

val DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR

@Component
class ErrorServiceImpl(
        private val errorAttributes: ErrorAttributes,
        @Value("\${debug:false}") private val debug: Boolean
) : ErrorService {

    override fun createRecord(request: WebRequest): ErrorRecord {
        val error: Throwable? = errorAttributes.getError(request)
        val attributes = errorAttributes.getErrorAttributes(request, debug)

        if (error == null) {
            val status = HttpStatus.resolve(attributes["status"] as Int) ?: DEFAULT_STATUS
            val message = attributes["error"] as? String
            return ErrorRecord(status.value(), message)
        }

        val status = (error as? ApplicationRuntimeException)?.status ?: DEFAULT_STATUS.value()
        return ErrorRecord(status, error.message)
    }
}