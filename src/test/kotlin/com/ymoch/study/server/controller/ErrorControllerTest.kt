package com.ymoch.study.server.controller

import com.ymoch.study.server.exception.ApplicationRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ErrorControllerTest {

    private lateinit var controller: ErrorController

    @BeforeEach
    fun setUp() {
        controller = ErrorController()
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