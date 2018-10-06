package com.ymoch.study.server.service.impl

import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service
import org.springframework.web.context.WebApplicationContext

@Service
@Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class DebugServiceImpl(
        @Value("\${debugging:false}") private val debugMode: Boolean
) : DebugService {
    var requestDebugMode: Boolean = false

    override fun debugModeEnabled() = debugMode
    override fun enableRequestDebugMode() {
        if (!debugMode) {
            return
        }
        requestDebugMode = true
    }

    override fun requestDebugModeEnabled() = requestDebugMode
}
