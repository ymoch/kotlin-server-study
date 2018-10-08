package com.ymoch.study.server.service.debug.impl

import com.ymoch.study.server.service.JsonResponseEditor
import com.ymoch.study.server.record.debug.DebugRecord
import com.ymoch.study.server.service.debug.DebugRecorder
import com.ymoch.study.server.service.debug.DebugService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
@Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class DebugServiceImpl(
        private val conversionService: ConversionService,
        private val jsonResponseEditor: JsonResponseEditor,
        private val debugMode: Boolean,
        private val wrap: (HttpServletResponse) -> ContentCachingResponseWrapper
) : DebugService {

    @Autowired
    constructor(
            conversionService: ConversionService,
            jsonResponseEditor: JsonResponseEditor,
            @Value("\${debugging:false}") debugMode: Boolean
    ) : this(conversionService, jsonResponseEditor, debugMode, Companion::wrapDefault)

    companion object {
        fun wrapDefault(response: HttpServletResponse) =
                ContentCachingResponseWrapper(response)
    }

    // When request debug mode is on, then recorder is not null.
    // When request debug mode is off, then recorder is null.
    var recorder: DebugRecorder? = null

    override fun debugModeEnabled() = debugMode

    override fun isDebugRequest(request: HttpServletRequest): Boolean {
        val debugParameter = request.getParameter("_debug")
        return try {
            conversionService.convert(debugParameter, Boolean::class.java) ?: false
        } catch (ignored: ConversionException) {
            false
        }
    }

    override fun enableRequestDebugMode() {
        if (!debugMode) {
            return
        }
        recorder = DebugRecorder()
    }

    override fun createRequestDebugRecord(): DebugRecord? {
        return recorder?.toDebugRecord()
    }

    override fun debugRun(
            response: HttpServletResponse,
            run: (HttpServletResponse) -> Unit) {
        val responseWrapper = wrap(response)

        recorder = DebugRecorder()
        val record = try {
            run(responseWrapper)
            recorder?.toDebugRecord()
        } finally {
            recorder = null
        }

        record?.let {
            jsonResponseEditor.putField(responseWrapper, "_debug", record)
        }
        responseWrapper.copyBodyToResponse()
    }

    override fun getDebugRecorder() = recorder
}
