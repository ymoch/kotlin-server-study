package com.ymoch.study.server.filter

import com.ymoch.study.server.service.debug.DebugService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ConditionalOnProperty("debugging", havingValue = "true")
@Component
class DebugFilter(
        private val context: ApplicationContext
) : OncePerRequestFilter() {

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain) {
        // Debug service is request-scoped.
        val debugService = context.getBean(DebugService::class.java)
        if (!debugService.isDebugRequest(request)) {
            filterChain.doFilter(request, response)
            return
        }

        debugService.debugRun(response) { currentResponse ->
            filterChain.doFilter(request, currentResponse)
        }
    }
}
