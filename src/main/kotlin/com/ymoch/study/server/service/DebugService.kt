package com.ymoch.study.server.service

interface DebugService {
    fun debugModeEnabled(): Boolean
    fun enableRequestDebugMode()
    fun requestDebugModeEnabled(): Boolean
}
