package com.ymoch.study.server.service

import org.springframework.web.context.request.WebRequest

interface DebugService {
    fun isDebugging() = false
    fun isDebugging(request: WebRequest) = false
}
