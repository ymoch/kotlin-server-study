package com.ymoch.study.server.service

import com.ymoch.study.server.record.debug.DebugRecord
import javax.servlet.http.HttpServletRequest

interface DebugService {
    fun debugModeEnabled(): Boolean
    fun isDebugRequest(request: HttpServletRequest): Boolean
    fun enableRequestDebugMode()
    fun registerException(exception: Exception)
    fun createRequestDebugRecord(): DebugRecord?
}
