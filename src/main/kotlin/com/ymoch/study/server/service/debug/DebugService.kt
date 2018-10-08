package com.ymoch.study.server.service.debug

import com.ymoch.study.server.record.debug.DebugRecord
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface DebugService {

    fun debugModeEnabled(): Boolean
    fun isDebugRequest(request: HttpServletRequest): Boolean
    fun enableRequestDebugMode()
    fun createRequestDebugRecord(): DebugRecord?

    fun debugRun(
            response: HttpServletResponse,
            run: (HttpServletResponse) -> Unit)

    fun getDebugRecorder(): DebugRecorder?
}
