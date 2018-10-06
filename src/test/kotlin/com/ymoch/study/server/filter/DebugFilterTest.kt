package com.ymoch.study.server.filter

import com.ymoch.study.server.service.DebugService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.core.convert.ConversionException
import org.springframework.core.convert.ConversionService
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class DebugFilterTest {

    @Mock
    private lateinit var conversionService: ConversionService

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
        filter = DebugFilter(conversionService)
        filter.setDebugService(debugService)

        `when`(conversionService.convert(null, Boolean::class.java)).thenReturn(null)
        `when`(conversionService.convert("on", Boolean::class.java)).thenReturn(true)
        `when`(conversionService.convert("off", Boolean::class.java)).thenReturn(false)
        `when`(conversionService.convert("invalid", Boolean::class.java))
                .thenThrow(mock(ConversionException::class.java))
    }

    @Nested
    inner class WhenDebugModeIsNotEnabled {

        @BeforeEach
        fun setUp() {
            `when`(debugService.debugModeEnabled()).thenReturn(false)
        }

        @Test
        fun thenRunsDefaultFilterChain() = testRunsDefaultFilterChain(null)
    }

    @Nested
    inner class WhenDebugModeIsEnabled {
        @BeforeEach
        fun setUp() {
            `when`(debugService.debugModeEnabled()).thenReturn(true)
        }

        @Nested
        inner class WhenNotGivenDebugParameter {
            @Test
            fun thenRunsDefaultFilterChain() =
                    testRunsDefaultFilterChain(null)
        }

        @Nested
        inner class WhenGivenNotDebuggingParameter {
            @Test
            fun thenRunsDefaultFilterChain() =
                    testRunsDefaultFilterChain("off")
        }

        @Nested
        inner class WhenGivenInvalidDebuggingParameter {
            @Test
            fun thenRunsDefaultFilterChain() =
                    testRunsDefaultFilterChain("invalid")
        }

    }

    private fun testRunsDefaultFilterChain(debugParam: String?) {
        `when`(request.getParameter("_debug")).thenReturn(debugParam)
        filter.doFilter(request, response, filterChain)
        verify(filterChain, times(1)).doFilter(request, response)
    }
}
