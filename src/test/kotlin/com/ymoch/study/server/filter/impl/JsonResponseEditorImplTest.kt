package com.ymoch.study.server.filter.impl

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.ByteArrayInputStream
import java.io.PrintWriter

internal class JsonResponseEditorImplTest {

    @Mock
    private lateinit var responseWrapper: ContentCachingResponseWrapper

    @Mock
    private lateinit var writer: PrintWriter

    private lateinit var editor: JsonResponseEditorImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        editor = JsonResponseEditorImpl()
        `when`(responseWrapper.writer).thenReturn(writer)
    }

    @Nested
    inner class WhenGivenListResponse {

        @BeforeEach
        fun setUp() {
            val content = "[]".toByteArray(Charsets.UTF_8)
            `when`(responseWrapper.contentInputStream)
                    .thenReturn(ByteArrayInputStream(content))
        }

        @Test
        fun thenDoNothing() {
            editor.put(responseWrapper, "key", "value")
            verify(responseWrapper, never()).outputStream
        }
    }
}