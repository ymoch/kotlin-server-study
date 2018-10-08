package com.ymoch.study.server.filter

import com.ymoch.study.server.service.DebugService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.context.ApplicationContext
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class DebugFilterTest {

    @Mock
    private lateinit var context: ApplicationContext

    @Mock
    private lateinit var jsonResponseEditor: JsonResponseEditor

    @Mock
    private lateinit var debugService: DebugService

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var filterChain: FilterChain

    private lateinit var filter: DebugFilter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(context.getBean(DebugService::class.java)).thenReturn(debugService)
    }

    @Nested
    inner class WhenDebugModeIsNotEnabled {

        @BeforeEach
        fun setUp() {
            // Run the secondary constructor
            // because the wrap function will not be used in this case.
            filter = DebugFilter(context, jsonResponseEditor)
            `when`(debugService.debugModeEnabled()).thenReturn(false)
        }

        @Test
        fun thenRunsDefaultFilterChain() = testRunsDefaultFilterChain()
    }

    @Nested
    inner class WhenDebugModeIsEnabled {

        @Mock
        private lateinit var wrap: (HttpServletResponse) -> ContentCachingResponseWrapper

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)

            filter = DebugFilter(context, jsonResponseEditor, wrap)
            `when`(debugService.debugModeEnabled()).thenReturn(true)
        }

        @Nested
        inner class WhenGivenNotDebugRequest {

            @BeforeEach
            fun setUp() {
                `when`(debugService.isDebugRequest(request)).thenReturn(false)
            }

            @Test
            fun thenRunsDefaultFilterChain() = testRunsDefaultFilterChain()
        }

        @Nested
        inner class WhenGivenDebugRequest {
            @BeforeEach
            fun setUp() {
                `when`(debugService.isDebugRequest(request)).thenReturn(true)
            }
        }
    }

    private fun testRunsDefaultFilterChain() {
        filter.doFilter(request, response, filterChain)
        verify(filterChain, times(1)).doFilter(request, response)
    }
}
