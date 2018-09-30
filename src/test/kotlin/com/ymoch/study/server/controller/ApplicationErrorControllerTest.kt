package com.ymoch.study.server.controller

import com.ymoch.study.server.entity.ErrorRecord
import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.ErrorService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.web.context.request.WebRequest
import javax.servlet.http.HttpServletResponse

internal class ApplicationErrorControllerTest {

    @Mock
    private lateinit var errorService: ErrorService

    private lateinit var controller: ApplicationErrorController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        controller = ApplicationErrorController(errorService)
    }

    @Nested
    inner class GetErrorPathTest {
        @Test
        fun returnsTheErrorPath() {
            assertThat(controller.errorPath, equalTo("/error"))
        }
    }

    @Nested
    inner class ShowErrorTest {
        @Mock
        private lateinit var request: WebRequest

        @Mock
        private lateinit var response: HttpServletResponse

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)
        }

        @Test
        fun returnsErrorDocument() {
            `when`(errorService.createRecord(request))
                    .thenReturn(ErrorRecord(status = 500, error = "error", message = "message"))

            val record = controller.showError(request, response)
            assertThat(record.status, equalTo(500))
            assertThat(record.error, equalTo("error"))
            assertThat(record.message, equalTo("message"))

            verify(response, times(1)).status = 500
        }
    }

    @Nested
    inner class RaiseErrorTest {
        @Test
        fun raisesApplicationRuntimeException() {
            val exception = assertThrows<ApplicationRuntimeException> { controller.raiseError() }
            assertThat(exception.status, equalTo(400))
            assertThat(exception.message, equalTo("An expected error occurred."))
        }
    }

    @Nested
    inner class RaiseErrorUnexpectedTest {
        @Test
        fun raisesRuntimeException() {
            val exception = assertThrows<RuntimeException> { controller.raiseErrorUnexpected() }
            assertThat(exception.message, equalTo("An unexpected error occurred."))
        }
    }
}