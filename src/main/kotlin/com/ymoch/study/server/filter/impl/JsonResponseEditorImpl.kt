package com.ymoch.study.server.filter.impl

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymoch.study.server.filter.JsonResponseEditor
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class JsonResponseEditorImpl(
        private val objectMapper: ObjectMapper
) : JsonResponseEditor {

    override fun putFields(
            responseWrapper: ContentCachingResponseWrapper,
            map: Map<String, Any?>) {
        val inStream = responseWrapper.contentInputStream
        val jsonObject = try {
            objectMapper.readValue<LinkedHashMap<String, Any?>>(inStream)
        } catch (ignored: JsonMappingException) {
            return
        }

        map.entries.forEach {
            jsonObject[it.key] = it.value
        }

        responseWrapper.reset()
        objectMapper.writeValue(responseWrapper.writer, jsonObject)
    }
}
