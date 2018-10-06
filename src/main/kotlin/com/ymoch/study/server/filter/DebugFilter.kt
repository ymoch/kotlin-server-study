package com.ymoch.study.server.filter

import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

@Component
class DebugFilter(
        private val conversionService: ConversionService
) : Filter {

    @Autowired // Since this service's scope is request.
    lateinit var debugService: DebugService

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val isDebugRequest = debugService.debugModeEnabled() &&
                request != null &&
                isDebugParameterEnabled(request, conversionService)
        if (!isDebugRequest) {
            chain?.doFilter(request, response)
            return
        }

        debugService.enableRequestDebugMode()
        chain?.doFilter(request, response)
    }

    override fun init(filterConfig: FilterConfig?) {
        // Do nothing.
    }

    override fun destroy() {
        // Do nothing.
    }
}

fun isDebugParameterEnabled(
        request: ServletRequest,
        conversionService: ConversionService
): Boolean {
    val debugParameter: String = request.getParameter("_debug") ?: return false
    return try {
        conversionService.convert(debugParameter, Boolean::class.java) ?: false
    } catch (ignored: IllegalArgumentException) {
        false
    }
}
