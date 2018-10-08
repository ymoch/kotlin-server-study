package com.ymoch.study.server.service.impl

import com.ymoch.study.server.record.debug.DebugRecord
import com.ymoch.study.server.service.debug.DebugService
import com.ymoch.study.server.service.debug.JsonResponseEditor
import com.ymoch.study.server.service.debug.impl.DebugServiceImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class DebugServiceImplTest {

    @Mock
    private lateinit var conversionService: ConversionService

    @Mock
    private lateinit var jsonResponseEditor: JsonResponseEditor

    private lateinit var service: DebugService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Nested
    inner class WhenGivenRequest {

        @Mock
        private lateinit var request: HttpServletRequest

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)

            // Use the secondary constructor
            // because the response wrap function is not used in this case.
            service = DebugServiceImpl(conversionService, jsonResponseEditor, false)

            `when`(conversionService.convert(null, Boolean::class.java))
                    .thenReturn(null)
            `when`(conversionService.convert("off", Boolean::class.java))
                    .thenReturn(false)
            `when`(conversionService.convert("on", Boolean::class.java))
                    .thenReturn(false)
            `when`(conversionService.convert("invalid", Boolean::class.java))
                    .thenThrow(mock(ConversionException::class.java))
        }

        @Nested
        inner class WhenGivenNoDebugParameter {
            @Test
            fun thenIsNotDebugRequest() {
                testIsDebugRequest(null, false)
            }
        }

        @Nested
        inner class WhenGivenFalseDebugParameter {
            @Test
            fun thenIsNotDebugRequest() {
                testIsDebugRequest("off", false)
            }
        }

        @Nested
        inner class WhenGivenTrueDebugParameter {
            @Test
            fun thenIsDebugRequest() {
                testIsDebugRequest("on", false)
            }
        }

        @Nested
        inner class WhenGivenInvalidDebugParameter {
            @Test
            fun thenIsDebugRequest() {
                testIsDebugRequest("invalid", false)
            }
        }

        private fun testIsDebugRequest(parameter: String?, expected: Boolean) {
            `when`(request.getParameter("_debug")).thenReturn(parameter)

            val actual = service.isDebugRequest(request)
            assertThat(actual, equalTo(expected))
            verify(request, times(1)).getParameter("_debug")
            verify(conversionService, times(1)).convert(parameter, Boolean::class.java)
        }
    }

    @Nested
    inner class WhenDebuggingPropertyIsFalse {

        @BeforeEach
        fun setUp() {
            // Use the secondary constructor
            // because the response wrap function is not used in this case.
            service = DebugServiceImpl(conversionService, jsonResponseEditor, false)
        }

        @Test
        fun thenDebugModeIsNotEnabled() {
            assertThat(service.debugModeEnabled(), equalTo(false))
        }
    }

    @Nested
    inner class WhenDebuggingPropertyIsTrue {

        @BeforeEach
        fun setUp() {
            // Use the secondary constructor
            // because the response wrap function is not used in this case.
            service = DebugServiceImpl(conversionService, jsonResponseEditor, true)
        }

        @Test
        fun thenDebugModeIsEnabled() {
            assertThat(service.debugModeEnabled(), equalTo(true))
        }
    }

    @Nested
    inner class WhenDebugging {

        @Mock
        private lateinit var wrap:
                (HttpServletResponse) -> ContentCachingResponseWrapper

        @Mock
        private lateinit var response: HttpServletResponse

        @Mock
        private lateinit var wrappedResponse: ContentCachingResponseWrapper

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)

            service = DebugServiceImpl(
                    conversionService, jsonResponseEditor, true, wrap)

            `when`(wrap(response)).thenReturn(wrappedResponse)
        }

        @Test
        fun test() {
            var record: DebugRecord? = null
            service.debugRun(response) {
                val recorder = service.getDebugRecorder()!!
                recorder.registerException(Exception("message"))
                record = recorder.toDebugRecord()
            }

            verify(wrappedResponse, times(1)).copyBodyToResponse()
            verify(jsonResponseEditor, times(1))
                    .putField(wrappedResponse, "_debug", record)
        }
    }

    @Test
    fun wrapDefaultWrapsResponse() {
        val response = mock(HttpServletResponse::class.java)
        val wrappedResponse = DebugServiceImpl.wrapDefault(response)
        val innerResponse = wrappedResponse.response
        assertThat(innerResponse as HttpServletResponse, equalTo(response))
    }
}