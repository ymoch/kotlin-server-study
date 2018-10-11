package com.ymoch.study.server.record.greeting

data class GreetingRecord(
        val message: String,
        val target: String? = null
)