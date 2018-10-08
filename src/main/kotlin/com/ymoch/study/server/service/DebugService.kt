package com.ymoch.study.server.service

import com.ymoch.study.server.record.debug.DebugRecord
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface DebugService {

    fun debugModeEnabled(): Boolean
    fun isDebugRequest(request: HttpServletRequest): Boolean
    fun enableRequestDebugMode()
    fun registerException(exception: Exception)
    fun createRequestDebugRecord(): DebugRecord?

    fun debugRun(
            response: HttpServletResponse,
            run: (HttpServletResponse) -> Unit)
}
