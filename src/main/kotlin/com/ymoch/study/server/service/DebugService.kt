package com.ymoch.study.server.service

import com.ymoch.study.server.record.debug.DebugRecord

interface DebugService {
    fun debugModeEnabled(): Boolean
    fun enableRequestDebugMode()
    fun registerException(exception: Exception)
    fun createRequestDebugRecord(): DebugRecord?
}
