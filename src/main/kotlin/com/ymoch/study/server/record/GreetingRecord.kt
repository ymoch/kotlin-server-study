package com.ymoch.study.server.record

data class GreetingRecord(
        val message: String,
        val target: String? = null
)