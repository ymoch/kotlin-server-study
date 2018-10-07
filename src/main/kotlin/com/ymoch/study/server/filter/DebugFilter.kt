package com.ymoch.study.server.filter

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

val DEFAULT_RESPONSE_WRAPPER_CREATOR = { response: HttpServletResponse ->
    ContentCachingResponseWrapper(response)
}

@Component
class DebugFilter(
        private val context: ApplicationContext,
        private val conversionService: ConversionService,
        private val responseWrapperCreator: (HttpServletResponse) -> ContentCachingResponseWrapper
) : OncePerRequestFilter() {

    @Autowired
    constructor(
            context: ApplicationContext,
            conversionService: ConversionService
    ) : this(context, conversionService, DEFAULT_RESPONSE_WRAPPER_CREATOR)

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain) {
        // Debug service is request-scoped.
        val debugService = context.getBean(DebugService::class.java)

        val isDebugRequest = debugService.debugModeEnabled() &&
                isDebugParameterEnabled(request, conversionService)
        if (!isDebugRequest) {
            filterChain.doFilter(request, response)
            return
        }
        debugService.enableRequestDebugMode()

        val responseWrapper = responseWrapperCreator(response)
        try {
            filterChain.doFilter(request, responseWrapper)
            addDebugRecord(responseWrapper, debugService)
        } finally {
            responseWrapper.copyBodyToResponse()
        }
    }

    private fun addDebugRecord(
            responseWrapper: ContentCachingResponseWrapper,
            debugService: DebugService) {
        val mapper = ObjectMapper()
        val inStream = responseWrapper.contentInputStream
        val map = try {
            mapper.readValue<LinkedHashMap<String, Any?>>(inStream)
        } catch (ignored: JsonMappingException) {
            return
        }
        debugService.createRequestDebugRecord()?.let {
            map["_debug"] = it
        }

        responseWrapper.reset()
        mapper.writeValue(responseWrapper.outputStream, map)
    }
}

fun isDebugParameterEnabled(
        request: ServletRequest,
        conversionService: ConversionService
): Boolean {
    val debugParameter = request.getParameter("_debug")
    return try {
        conversionService.convert(debugParameter, Boolean::class.java) ?: false
    } catch (ignored: ConversionException) {
        false
    }
}
