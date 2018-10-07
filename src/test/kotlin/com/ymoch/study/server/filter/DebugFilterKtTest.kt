package com.ymoch.study.server.filter

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

internal class DebugFilterKtTest {

    @Nested
    inner class WrapDefaultlyTest {

        @Mock
        private lateinit var response: HttpServletResponse

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)
        }

        @Test
        fun createsWrapper() {
            val wrappedResponse = wrapDefaultly(response)

            val innerResponse = wrappedResponse.response
            assert(innerResponse is HttpServletResponse)
            assertThat(innerResponse, equalTo(response as ServletResponse))
        }
    }
}