package com.ymoch.study.server.service.impl

import com.ymoch.study.server.record.ErrorRecord
import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.ErrorService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.servlet.NoHandlerFoundException
import java.lang.Exception

val DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR

@Service
class ErrorServiceImpl : ErrorService {

    override fun createRecord(exception: Exception): ErrorRecord {
        val status = when (exception) {
            is ApplicationRuntimeException -> exception.status
            is NoHandlerFoundException -> HttpStatus.NOT_FOUND.value()
            else -> DEFAULT_STATUS.value()
        }
        return ErrorRecord(status, exception.message)
    }
}
