package com.ymoch.study.server.filter

import org.springframework.web.util.ContentCachingResponseWrapper

interface JsonResponseEditor {
    fun putField(
            responseWrapper: ContentCachingResponseWrapper,
            key: String,
            value: Any?
    ) = putFields(responseWrapper, mapOf(key to value))

    fun putFields(
            responseWrapper: ContentCachingResponseWrapper,
            map: Map<String, Any?>)
}
