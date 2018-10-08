package com.ymoch.study.server.service.impl

import com.ymoch.study.server.service.debug.JsonResponseEditor
import com.ymoch.study.server.record.debug.DebugRecord
import com.ymoch.study.server.record.debug.ExceptionRecord
import com.ymoch.study.server.service.debug.DebugService
import com.ymoch.study.server.service.debug.impl.DebugServiceImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
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

        @Test
        fun thenDoesNotCreateDebugRecord() {
            service.enableRequestDebugMode()

            val record = service.createRequestDebugRecord()
            assertThat(record, equalTo(null as DebugRecord?))
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

        @Nested
        inner class WhenRequestDebugModeIsNotEnabled {

            @Test
            fun thenDoesNotCreateRecord() {
                val record = service.createRequestDebugRecord()
                assertThat(record, equalTo(null as DebugRecord?))
            }
        }

        @Nested
        inner class WhenRequestDebugModeIsEnabled {

            @BeforeEach
            fun setUp() {
                service.enableRequestDebugMode()
            }

            @Nested
            inner class WhenNoDataAreSet {

                @Test
                fun thenCreatesEmptyRecord() {
                    val record = service.createRequestDebugRecord()!!
                    assertThat(record.exception, equalTo(null as ExceptionRecord?))
                }
            }

            @Nested
            inner class WhenDataAreSet {

                lateinit var exception: Exception

                @BeforeEach
                fun setUp() {
                    exception = mock(Exception::class.java)
                    `when`(exception.stackTrace).thenReturn(arrayOf(
                            StackTraceElement("declaringClass", "methodName", null, -2),
                            StackTraceElement("declaringClass", "methodName", null, -1),
                            StackTraceElement("declaringClass", "methodName", "fileName", -1),
                            StackTraceElement("declaringClass", "methodName", "fileName", 0),
                            StackTraceElement("declaringClass", "methodName", "fileName", 1)
                    ))
                    service.getDebugRecorder()?.registerException(exception)
                }

                @Test
                fun thenCreatesRecord() {
                    val record = service.createRequestDebugRecord()!!
                    val exceptionRecord = record.exception!!
                    assertThat(exceptionRecord.stackTrace, hasSize(5))
                    assertThat(exceptionRecord.stackTrace[0],
                            equalTo("declaringClass.methodName(Native Method)"))
                    assertThat(exceptionRecord.stackTrace[1],
                            equalTo("declaringClass.methodName(Unknown Source)"))
                    assertThat(exceptionRecord.stackTrace[2],
                            equalTo("declaringClass.methodName(fileName)"))
                    assertThat(exceptionRecord.stackTrace[3],
                            equalTo("declaringClass.methodName(fileName:0)"))
                    assertThat(exceptionRecord.stackTrace[4],
                            equalTo("declaringClass.methodName(fileName:1)"))
                }
            }

            @Nested
            inner class WhenExceptionIsSet {

                @Test
                fun thenSetsExceptionClassName() {
                    service.getDebugRecorder()?.registerException(Exception())
                    val record = service.createRequestDebugRecord()!!
                    val exceptionRecord = record.exception!!
                    assertThat(exceptionRecord.className, equalTo("java.lang.Exception"))
                }
            }
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
    }

    @Test
    fun wrapDefaultWrapsResponse() {
        val response = mock(HttpServletResponse::class.java)
        val wrappedResponse = DebugServiceImpl.wrapDefault(response)
        val innerResponse = wrappedResponse.response
        assertThat(innerResponse as HttpServletResponse, equalTo(response))
    }
}