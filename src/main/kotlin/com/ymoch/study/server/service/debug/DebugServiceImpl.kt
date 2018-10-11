package com.ymoch.study.server.service.debug

import org.springframework.context.annotation.Scope
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import org.springframework.web.context.WebApplicationContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
@Scope(WebApplicationContext.SCOPE_REQUEST)
internal class DebugServiceImpl(
        private val conversionService: ConversionService,
        private val jsonResponseEditor: JsonResponseEditor,
        private val responseWrapperFactory: ResponseWrapperFactory
) : DebugService {

    // When request debug mode is on, then recorder is not null.
    // When request debug mode is off, then recorder is null.
    var recorder: DebugRecorder? = null

    override fun isDebugRequest(request: HttpServletRequest): Boolean {
        val debugParameter = request.getParameter("_debug")
        return try {
            conversionService.convert(debugParameter, Boolean::class.java) ?: false
        } catch (ignored: ConversionException) {
            false
        }
    }

    override fun debugRun(
            response: HttpServletResponse,
            run: (HttpServletResponse) -> Unit) {
        val responseWrapper = responseWrapperFactory.wrap(response)

        val currentRecorder = DebugRecorder()

        recorder = currentRecorder
        val record = try {
            run(responseWrapper)
            currentRecorder.toDebugRecord()
        } finally {
            recorder = null
        }

        jsonResponseEditor.putField(responseWrapper, "_debug", record)
        responseWrapper.copyBodyToResponse()
    }

    override fun getDebugRecorder() = recorder
}
