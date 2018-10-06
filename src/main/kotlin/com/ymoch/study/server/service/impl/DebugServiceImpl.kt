package com.ymoch.study.server.service.impl

import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import org.springframework.web.context.request.WebRequest

private class EmptyDebugService : DebugService

private class RequestDebugService(
        private val conversionService: ConversionService
) : DebugService {
    override fun isDebugging() = true
    override fun isDebugging(request: WebRequest): Boolean {
        val debugParam = request.getParameter("_debug") ?: return false
        return try {
            conversionService.convert(debugParam, Boolean::class.java) ?: false
        } catch (ignored: IllegalArgumentException) {
            false
        }
    }
}

fun decideDerivingService(debugging: Boolean, conversionService: ConversionService) = if (!debugging) {
    EmptyDebugService()
} else {
    RequestDebugService(conversionService)
}

@Service
class DebugServiceImpl(
        @Value("\${debugging:false}") debugging: Boolean,
        conversionService: ConversionService
) : DebugService by decideDerivingService(debugging, conversionService)
