package com.ymoch.study.server.configuration

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.InjectionPoint
import org.springframework.core.MethodParameter
import java.util.logging.Logger

internal class LoggerConfigurationTest {

    @Mock
    private lateinit var injectionPoint: InjectionPoint

    private lateinit var loggerConfiguration: LoggerConfiguration

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loggerConfiguration = LoggerConfiguration()
    }

    @Nested
    inner class WhenInjectionPointHasMethodParameter {

        @Mock
        private lateinit var methodParameter: MethodParameter

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.initMocks(this)
            `when`(injectionPoint.methodParameter).thenReturn(methodParameter)
        }

        @Nested
        inner class WhenMethodParameterContainsClass {

            @BeforeEach
            fun setUp() {
                `when`(methodParameter.containingClass)
                        .thenReturn(LoggerConfigurationTest::class.java)
            }

            @Test
            fun thenReturnsLogger() = testCreatesLogger()
        }

        @Nested
        inner class WhenMethodParameterContainsNoClass {

            @BeforeEach
            fun setUp() {
                `when`(methodParameter.containingClass).thenReturn(null)
            }


            @Nested
            inner class WhenInjectionPointHasNoField {

                @BeforeEach
                fun setUp() {
                    `when`(injectionPoint.field).thenReturn(null)
                }

                @Test
                fun thenThrowException() = testThrowsException()
            }
        }
    }

    @Nested
    inner class WhenInjectionPointHasNoMethodParameter {

        @BeforeEach
        fun setUp() {
            `when`(injectionPoint.methodParameter).thenReturn(null)
        }

        @Nested
        inner class WhenInjectionPointHasField {

            private var logger: Logger? = null

            @BeforeEach
            fun setUp() {
                val field = WhenInjectionPointHasField::class.java
                        .getDeclaredField("logger")
                `when`(injectionPoint.field).thenReturn(field)
            }

            @Test
            fun thenReturnsLogger() = testCreatesLogger()
        }

        @Nested
        inner class WhenInjectionPointHasNoField {

            @BeforeEach
            fun setUp() {
                `when`(injectionPoint.field).thenReturn(null)
            }

            @Test
            fun thenThrowException() = testThrowsException()
        }
    }

    fun testCreatesLogger() {
        loggerConfiguration.logger(injectionPoint)
    }

    fun testThrowsException() {
        assertThrows(BeanCreationException::class.java) {
            loggerConfiguration.logger(injectionPoint)
        }
    }
}