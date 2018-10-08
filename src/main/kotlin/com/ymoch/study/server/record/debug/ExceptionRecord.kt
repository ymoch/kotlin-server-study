package com.ymoch.study.server.record.debug

data class ExceptionRecord(
        val className: String,
        val stackTrace: List<String>
)