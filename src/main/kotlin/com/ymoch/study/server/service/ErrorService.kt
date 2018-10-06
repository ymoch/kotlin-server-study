package com.ymoch.study.server.service

import com.ymoch.study.server.record.ErrorRecord
import org.springframework.web.context.request.WebRequest
import java.lang.Exception

interface ErrorService {
    fun createRecord(exception: Exception, request: WebRequest): ErrorRecord
}