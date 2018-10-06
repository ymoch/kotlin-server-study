package com.ymoch.study.server.controller

import com.ymoch.study.server.record.ErrorRecord
import com.ymoch.study.server.service.DebugService
import com.ymoch.study.server.service.ErrorService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import javax.servlet.http.HttpServletResponse

internal class ErrorHandlingControllerTest {

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
        controller = ErrorHandlingController(errorService)
        controller.setDebugService(debugService)
    }

    @Test
    fun setsExceptionAndReturnsGivenErrorRecord() {
        val exception = Exception()
        `when`(errorService.createRecord(exception))
                .thenReturn(ErrorRecord(403, "error", "message"))

        val record = controller.handleException(exception, response)
        assertThat(record.status, equalTo(403))
        assertThat(record.error, equalTo("error"))
        assertThat(record.message, equalTo("message"))

        verify(debugService).registerException(exception)
        verify(response).status = 403
    }
}
