package com.ymoch.study.server.controller

import com.ymoch.study.server.record.ErrorRecord
import com.ymoch.study.server.service.DebugService
import com.ymoch.study.server.service.ErrorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ErrorHandlingController(val errorService: ErrorService) {

    // Since this service's scope is request.
    private lateinit var debugService: DebugService

    @Autowired
    fun setDebugService(debugService: DebugService) {
        this.debugService = debugService
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
            exception: Exception,
            response: HttpServletResponse
    ): ErrorRecord {
        debugService.registerException(exception)

        val record = errorService.createRecord(exception)
        response.status = record.status
        return record
    }
}
