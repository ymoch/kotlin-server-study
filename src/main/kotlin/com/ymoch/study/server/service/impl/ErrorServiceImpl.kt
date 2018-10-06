package com.ymoch.study.server.service.impl

import com.ymoch.study.server.record.ErrorRecord
import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.ErrorService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.servlet.NoHandlerFoundException
import java.lang.Exception
import java.util.Arrays
import java.util.stream.Collectors

val DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR

@Service
class ErrorServiceImpl(
        @Value("\${debugging:false}") private val debugging: Boolean
) : ErrorService {

    override fun createRecord(exception: Exception): ErrorRecord {
        val status = when (exception) {
            is ApplicationRuntimeException -> exception.status
            is NoHandlerFoundException -> HttpStatus.NOT_FOUND.value()
            else -> DEFAULT_STATUS.value()
        }
        val exceptionName = if (debugging) exception::class.qualifiedName else null
        val stackTrace: List<String>? = if (debugging) {
            Arrays.stream(exception.stackTrace)
                    .map { toStackTraceLine(it) }
                    .collect(Collectors.toList())
        } else null
        return ErrorRecord(status, exception.message, exceptionName, stackTrace)
    }
}

fun toStackTraceLine(element: StackTraceElement): String {
    val source = when {
        element.isNativeMethod -> "Native Method"
        element.fileName == null -> "Unknown Source"
        element.lineNumber < 0 -> element.fileName
        else -> "${element.fileName}:${element.lineNumber}"
    }
    return "${element.className}.${element.methodName}($source)"
}
