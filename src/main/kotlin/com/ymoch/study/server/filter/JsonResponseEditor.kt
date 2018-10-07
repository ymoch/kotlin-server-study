package com.ymoch.study.server.filter

import org.springframework.web.util.ContentCachingResponseWrapper

interface JsonResponseEditor {
    fun put(
            responseWrapper: ContentCachingResponseWrapper,
            key: String, value: Any?)
}