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

    @Mock
    private lateinit var debugService: DebugService

    private lateinit var service: ErrorServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = ErrorServiceImpl()
        service.debugService = debugService
    }

    @Nested
    inner class WhenGivenRuntimeException {
        @Test
        fun thenSetsDefaultStatus() {
            `when`(debugService.debugModeEnabled()).thenReturn(true)
            val exception = RuntimeException("message")
            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(500))
            assertThat(record.message, equalTo("message"))
            verify(debugService, times(1)).requestDebugModeEnabled()
        }
    }

    @Nested
    inner class WhenGivenNormalApplicationRuntimeException {
        @Test
        fun thenSetsItsStatus() {
            `when`(debugService.debugModeEnabled()).thenReturn(false)
            val exception = ApplicationRuntimeException(
                    status = 503, message = "message", cause = RuntimeException("cause"))
            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(503))
            assertThat(record.message, equalTo("message"))
            verify(debugService, times(1)).requestDebugModeEnabled()
        }
    }

    @Nested
    inner class WhenGivenEmptyApplicationRuntimeException {
        @Test
        fun thenSetsItsStatus() {
            `when`(debugService.debugModeEnabled()).thenReturn(false)
            val exception = ApplicationRuntimeException()
            val record = service.createRecord(exception)
            assertThat(record.status, equalTo(500))
            assertThat(record.message, equalTo(null as String?))
            verify(debugService, times(1)).requestDebugModeEnabled()
        }
    }
}