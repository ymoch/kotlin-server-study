package com.ymoch.study.server.controller

import com.ymoch.study.server.record.error.ErrorRecord
import com.ymoch.study.server.service.debug.DebugService
import com.ymoch.study.server.service.error.ErrorService
import com.ymoch.study.server.service.debug.DebugRecorder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.context.ApplicationContext
import javax.servlet.http.HttpServletResponse

internal class ErrorHandlingControllerTest {

    @Mock
    private lateinit var context: ApplicationContext

    @Mock
    private lateinit var errorService: ErrorService

    @Mock
    private lateinit var debugService: DebugService

    @Mock
    private lateinit var response: HttpServletResponse

    private lateinit var controller: ErrorHandlingController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        controller = ErrorHandlingController(context, errorService)

        `when`(context.getBean(DebugService::class.java)).thenReturn(debugService)
        `when`(debugService.getDebugRecorder()).thenReturn(null)
    }

    @Test
    fun thenSetsExceptionAndReturnsGivenErrorRecord() {
        val exception = Exception()
        `when`(errorService.createRecord(exception))
                .thenReturn(ErrorRecord(403, "error", "message"))

        val record = controller.handleException(exception, response)
        assertThat(record.status, equalTo(403))
        assertThat(record.error, equalTo("error"))
        assertThat(record.message, equalTo("message"))

        verify(context, times(1)).getBean(DebugService::class.java)
        verify(debugService, times(1)).getDebugRecorder()
        verify(response, times(1)).status = 403
    }

    @Nested
    inner class WhenDebugging {

        private lateinit var debugRecorder: DebugRecorder

        @BeforeEach
        fun setUp() {
            debugRecorder = DebugRecorder()
            `when`(debugService.getDebugRecorder()).thenReturn(debugRecorder)
        }

        @Test
        fun then() {
            val exception = Exception()
            `when`(errorService.createRecord(exception))
                    .thenReturn(ErrorRecord(403, "error", "message"))
            controller.handleException(exception, response)

            val debugRecord = debugRecorder.toDebugRecord()
            assertThat(debugRecord.exception, notNullValue())
        }
    }
}
