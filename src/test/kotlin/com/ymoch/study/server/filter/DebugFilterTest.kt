package com.ymoch.study.server.filter

import com.ymoch.study.server.MockitoHelper.Companion.any
import com.ymoch.study.server.service.DebugService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.context.ApplicationContext
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class DebugFilterTest {

    @Mock
    private lateinit var context: ApplicationContext

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
        filter = DebugFilter(context)

        `when`(context.getBean(DebugService::class.java)).thenReturn(debugService)
        `when`(debugService.debugRun(any(), any())).thenAnswer {
            val res = it.getArgument<HttpServletResponse>(0)
            val run = it.getArgument<(HttpServletResponse) -> Unit>(1)
            res.setHeader("debugging", "on")
            run(res)
        }
    }

    @Nested
    inner class WhenDebugModeIsNotEnabled {

        @BeforeEach
        fun setUp() {
            `when`(debugService.debugModeEnabled()).thenReturn(false)
        }

        @Test
        fun thenRunsDefaultFilterChain() = testRunsDefaultFilterChain()
    }

    @Nested
    inner class WhenDebugModeIsEnabled {

        @BeforeEach
        fun setUp() {
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

            @Test
            fun thenRunsDebug() {
                filter.doFilter(request, response, filterChain)
                verify(filterChain, times(1)).doFilter(request, response)
                verify(response).setHeader("debugging", "on")
            }
        }
    }

    private fun testRunsDefaultFilterChain() {
        filter.doFilter(request, response, filterChain)
        verify(filterChain, times(1)).doFilter(request, response)
        verify(response, never()).setHeader(any(), any())
    }
}
