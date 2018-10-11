package com.ymoch.study.server.service.debug

import com.fasterxml.jackson.databind.ObjectMapper
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
        `when`(responseWrapper.writer).thenReturn(PrintWriter(writer))

        editor = JsonResponseEditorImpl(ObjectMapper())
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
            editor.putField(responseWrapper, "key", "value")
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
        fun thenAddAnItem() {
            editor.putField(responseWrapper, "key", "value")
            assertThat(writer.toString(),
                    equalTo("{\"foo\":\"bar\",\"key\":\"value\"}"))
        }

        @Test
        fun thenAddsItems() {
            editor.putFields(responseWrapper, mapOf("key" to "value"))
            assertThat(writer.toString(),
                    equalTo("{\"foo\":\"bar\",\"key\":\"value\"}"))
        }
    }
}