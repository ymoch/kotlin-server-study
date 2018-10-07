package com.ymoch.study.server.filter.impl

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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
import java.io.StringWriter

internal class JsonResponseEditorImplTest {

    @Mock
    private lateinit var responseWrapper: ContentCachingResponseWrapper

    private lateinit var writer: StringWriter

    private lateinit var editor: JsonResponseEditorImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        writer = StringWriter()
        editor = JsonResponseEditorImpl()
        `when`(responseWrapper.writer).thenReturn(PrintWriter(writer))
    }

    @Nested
    inner class WhenGivenNotJsonObjectResponse {

        @BeforeEach
        fun setUp() {
            val content = "[]".toByteArray(Charsets.UTF_8)
            `when`(responseWrapper.contentInputStream)
                    .thenReturn(ByteArrayInputStream(content))
        }

        @Test
        fun thenDoNothing() {
            editor.put(responseWrapper, "key", "value")
            verify(responseWrapper, never()).writer
        }
    }

    @Nested
    inner class WhenGivenJsonObjectResponse {

        @BeforeEach
        fun setUp() {
            val content = "{\"foo\":\"bar\"}".toByteArray(Charsets.UTF_8)
            `when`(responseWrapper.contentInputStream)
                    .thenReturn(ByteArrayInputStream(content))
        }

        @Test
        fun thenAddsItem() {
            editor.put(responseWrapper, "key", "value")
            assertThat(writer.toString(),
                    equalTo("{\"foo\":\"bar\",\"key\":\"value\"}"))
        }
    }
}