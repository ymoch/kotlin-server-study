package com.ymoch.study.server.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class LoggerConfiguration {

    @Bean
    @Scope("prototype")
    fun logger(injectionPoint: InjectionPoint): Logger {
        return LoggerFactory.getLogger(decideClass(injectionPoint))
    }

    private fun decideClass(injectionPoint: InjectionPoint): Class<*> {
        val methodParameter = injectionPoint.methodParameter
        if (methodParameter != null) {
            return methodParameter.containingClass
        }

        val field = injectionPoint.field
        if (field != null) {
            return field.declaringClass
        }

        throw BeanCreationException("Cannot find type for Logger.")
    }
}