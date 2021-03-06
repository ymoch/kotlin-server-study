package com.ymoch.study.server.controller

import com.ymoch.study.server.record.error.ErrorRecord
import com.ymoch.study.server.service.debug.DebugService
import com.ymoch.study.server.service.error.ErrorService
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ErrorHandlingController(
        private val context: ApplicationContext,
        private val errorService: ErrorService
) {

    @ExceptionHandler(Exception::class)
    fun handleException(
            exception: Exception,
            response: HttpServletResponse
    ): ErrorRecord {
        // Debug service is request-scoped.
        val debugService = context.getBean(DebugService::class.java)
        debugService.getDebugRecorder()?.registerException(exception)

        val record = errorService.createRecord(exception)
        response.status = record.status
        return record
    }
}
