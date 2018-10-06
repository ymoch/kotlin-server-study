package com.ymoch.study.server.service

import org.springframework.web.context.request.WebRequest

interface DebugService {
    fun isDebugging(): Boolean
    fun isDebugging(request: WebRequest): Boolean
}
