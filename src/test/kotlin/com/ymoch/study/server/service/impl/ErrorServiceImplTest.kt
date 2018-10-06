package com.ymoch.study.server.service.impl

import com.ymoch.study.server.exception.ApplicationRuntimeException
import com.ymoch.study.server.service.DebugService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

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