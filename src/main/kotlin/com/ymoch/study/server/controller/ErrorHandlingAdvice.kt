package com.ymoch.study.server.controller
import com.ymoch.study.server.record.ErrorRecord
import com.ymoch.study.server.service.ErrorService
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.lang.Exception
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ErrorHandlingAdvice (val errorService: ErrorService) {

    @ExceptionHandler(Exception::class)
    fun handleException(request: WebRequest, response: HttpServletResponse): ErrorRecord {
        val record = errorService.createRecord(request)
        response.status = record.status
        return record
    }
}