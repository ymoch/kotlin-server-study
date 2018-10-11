package com.ymoch.study.server.service.error

import com.ymoch.study.server.exception.ApplicationRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.web.servlet.NoHandlerFoundException

internal class ErrorServiceImplTest {

    private lateinit var service: ErrorServiceImpl

    @BeforeEach
    fun setUp() {
        service = ErrorServiceImpl()
    }

    @Nested
    inner class WhenGivenRuntimeException {
        @Test
        fun thenSetsDefaultStatus() {
            val exception = RuntimeException("message")
            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(500))
            assertThat(record.message, equalTo("message"))
        }
    }

    @Nested
    inner class WhenGivenNoHandlerFoundException {
        @Test
        fun thenSetsNotFoundStatus() {
            val exception = mock(NoHandlerFoundException::class.java)
            `when`(exception.message).thenReturn("message")

            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(404))
            assertThat(record.message, equalTo("message"))
        }
    }

    @Nested
    inner class WhenGivenNormalApplicationRuntimeException {
        @Test
        fun thenSetsItsStatus() {
            val exception = ApplicationRuntimeException(
                    status = 503, message = "message", cause = RuntimeException("cause"))
            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(503))
            assertThat(record.message, equalTo("message"))
        }
    }

    @Nested
    inner class WhenGivenEmptyApplicationRuntimeException {
        @Test
        fun thenSetsItsStatus() {
            val exception = ApplicationRuntimeException()
            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(500))
            assertThat(record.message, equalTo(null as String?))
        }
    }
}