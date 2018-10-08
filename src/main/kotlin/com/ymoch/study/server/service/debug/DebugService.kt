package com.ymoch.study.server.service.debug

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface DebugService {

    fun isDebugRequest(request: HttpServletRequest): Boolean

    fun debugRun(
            response: HttpServletResponse,
            run: (HttpServletResponse) -> Unit)

    fun getDebugRecorder(): DebugRecorder?
}
