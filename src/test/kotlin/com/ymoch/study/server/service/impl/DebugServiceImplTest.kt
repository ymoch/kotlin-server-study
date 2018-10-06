package com.ymoch.study.server.service.impl

import com.ymoch.study.server.record.debug.DebugRecord
import com.ymoch.study.server.record.debug.ExceptionRecord
import com.ymoch.study.server.service.DebugService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class DebugServiceImplTest {

    private lateinit var service: DebugService

    @Nested
    inner class WhenDebuggingPropertyIsFalse {

        @BeforeEach
        fun setUp() {
            service = DebugServiceImpl(false)
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
            service = DebugServiceImpl(true)
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
                    service.registerException(exception)
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
                    service.registerException(Exception())
                    val record = service.createRequestDebugRecord()!!
                    val exceptionRecord = record.exception!!
                    assertThat(exceptionRecord.className, equalTo("java.lang.Exception"))
                }
            }
        }
    }
}