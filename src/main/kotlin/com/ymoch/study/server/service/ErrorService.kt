package com.ymoch.study.server.service

import com.ymoch.study.server.record.ErrorRecord
import org.springframework.web.context.request.WebRequest

interface ErrorService {
    fun createRecord(request: WebRequest): ErrorRecord
}