package com.ymoch.study.server.service.impl

import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.context.request.WebRequest

@Service
class DebugServiceImpl(
        @Value("\${debugging:false}") private val debugging: Boolean
) : DebugService {

    override fun isDebugging(): Boolean = debugging

    override fun isDebugging(request: WebRequest): Boolean {
        if (!isDebugging()) {
            return false
        }

        // TODO: map value.
        return request.getParameter("_debug") != null
    }
}