package com.ymoch.study.server.service.impl

import com.ymoch.study.server.exception.ApplicationRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.MockitoAnnotations
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.web.context.request.WebRequest

internal class ErrorServiceImplTest {
    @Mock
    private lateinit var errorAttributes: ErrorAttributes
    @Mock
    private lateinit var request: WebRequest

    private lateinit var service: ErrorServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = ErrorServiceImpl(errorAttributes, false)
    }

    @Nested
    inner class WhenGivenNoException {

        @BeforeEach
        fun setUp() {
            `when`(errorAttributes.getError(any())).thenReturn(null)
        }

        @Nested
        inner class WhenGivenNoData {
            @Test
            fun thenDoesNotSetMessage() = test(
                    attributes = mapOf("status" to 403),
                    expectedStatus = 403,
                    expectedMessage = null
            )
        }

        @Nested
        inner class WhenGivenInvalidStatus {
            @Test
            fun thenSetsDefaultStatus() = test(
                    attributes = mapOf("status" to 999, "error" to "msgInvalid"),
                    expectedStatus = 500,
                    expectedMessage = "msgInvalid"
            )
        }

        @Nested
        inner class WhenGivenValidStatus {
            @Test
            fun thenSetsItsStatus() = test(
                    attributes = mapOf("status" to 400, "error" to "msg"),
                    expectedStatus = 400,
                    expectedMessage = "msg"
            )
        }

        private fun test(attributes: Map<String, Any?>, expectedStatus: Int, expectedMessage: String?) {
            `when`(errorAttributes.getErrorAttributes(request, false))
                    .thenReturn(attributes)

            val record = service.createRecord(request)
            assertThat(record.status, equalTo(expectedStatus))
            assertThat(record.message, equalTo(expectedMessage))
        }
    }

    @Nested
    inner class WhenGivenRuntimeException {
        @Test
        fun thenSetsDefaultStatus() {
            `when`(errorAttributes.getError(any()))
                    .thenReturn(RuntimeException("message"))

            val record = service.createRecord(request)
            assertThat(record.status, equalTo(500))
            assertThat(record.message, equalTo("message"))
        }
    }

    @Nested
    inner class WhenGivenApplicationRuntimeException {
        @Test
        fun thenSetsItsStatus() {
            `when`(errorAttributes.getError(any()))
                    .thenReturn(ApplicationRuntimeException(status = 503, message = "message"))

            val record = service.createRecord(request)
            assertThat(record.status, equalTo(503))
            assertThat(record.message, equalTo("message"))
        }
    }
}