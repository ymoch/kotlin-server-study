package com.ymoch.study.server.filter

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymoch.study.server.service.DebugService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class DebugFilter(
        private val conversionService: ConversionService,
        private val responseWrapperCreator: (HttpServletResponse) -> ContentCachingResponseWrapper
) : OncePerRequestFilter() {

    @Autowired
    constructor(conversionService: ConversionService)
            : this(conversionService, { response: HttpServletResponse ->
        ContentCachingResponseWrapper(response)
    })

    // Since this service's scope is request.
    private lateinit var debugService: DebugService

    @Autowired
    fun setDebugService(debugService: DebugService) {
        this.debugService = debugService
    }

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain) {
        val isDebugRequest = debugService.debugModeEnabled() &&
                isDebugParameterEnabled(request, conversionService)
        if (!isDebugRequest) {
            filterChain.doFilter(request, response)
            return
        }

        debugService.enableRequestDebugMode()

        val wrappedResponse = responseWrapperCreator(response)
        try {
            doFilterForWrappedResponse(request, wrappedResponse, filterChain)
        } finally {
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun doFilterForWrappedResponse(
            request: HttpServletRequest,
            wrappedResponse: ContentCachingResponseWrapper,
            filterChain: FilterChain) {
        filterChain.doFilter(request, wrappedResponse)

        val mapper = ObjectMapper()
        val inStream = wrappedResponse.contentInputStream
        val map = try {
            mapper.readValue<LinkedHashMap<String, Any?>>(inStream)
        } catch (ignored: JsonMappingException) {
            return
        }
        debugService.createRequestDebugRecord()?.let {
            map["_debug"] = it
        }
        wrappedResponse.reset()
        mapper.writeValue(wrappedResponse.outputStream, map)
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
