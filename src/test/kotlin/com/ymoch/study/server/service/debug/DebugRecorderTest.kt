package com.ymoch.study.server.service.debug

import com.ymoch.study.server.record.debug.ExceptionRecord
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class DebugRecorderTest {

    private lateinit var recorder: DebugRecorder

    @BeforeEach
    fun setUp() {
        recorder = DebugRecorder()
    }

    @Nested
    inner class WhenNoDataAreSet {
        @Test
        fun thenCreatesEmptyRecord() {
            val record = recorder.toDebugRecord()
            MatcherAssert.assertThat(record.exception, Matchers.equalTo(null as ExceptionRecord?))
        }
    }

    @Nested
    inner class WhenDataAreSet {

        lateinit var exception: Exception

        @BeforeEach
        fun setUp() {
            exception = mock(Exception::class.java)
            `when`(exception.message).thenReturn("message")
            `when`(exception.stackTrace).thenReturn(arrayOf(
                    StackTraceElement("declaringClass", "methodName", null, -2),
                    StackTraceElement("declaringClass", "methodName", null, -1),
                    StackTraceElement("declaringClass", "methodName", "fileName", -1),
                    StackTraceElement("declaringClass", "methodName", "fileName", 0),
                    StackTraceElement("declaringClass", "methodName", "fileName", 1)
            ))
            recorder.registerException(exception)
        }

        @Test
        fun thenCreatesRecord() {
            val record = recorder.toDebugRecord()
            val exceptionRecord = record.exception!!
            assertThat(exceptionRecord.message, equalTo("message"))
            assertThat(exceptionRecord.stackTrace, Matchers.hasSize(5))
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
            recorder.registerException(Exception())
            val record = recorder.toDebugRecord()
            val exceptionRecord = record.exception!!
            assertThat(exceptionRecord.className, Matchers.equalTo("java.lang.Exception"))
        }
    }

}