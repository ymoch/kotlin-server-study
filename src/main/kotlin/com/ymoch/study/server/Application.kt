package com.ymoch.study.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard

@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
