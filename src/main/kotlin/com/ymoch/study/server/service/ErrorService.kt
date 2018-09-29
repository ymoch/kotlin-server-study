package com.ymoch.study.server.service

import com.ymoch.study.server.entity.ErrorRecord
import org.springframework.web.context.request.WebRequest

interface ErrorService {
    fun createEntity(request: WebRequest): ErrorRecord
}