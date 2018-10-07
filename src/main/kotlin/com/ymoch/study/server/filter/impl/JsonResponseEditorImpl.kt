package com.ymoch.study.server.filter.impl

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ymoch.study.server.filter.JsonResponseEditor
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class JsonResponseEditorImpl : JsonResponseEditor {

    override fun put(
            responseWrapper: ContentCachingResponseWrapper,
            key: String, value: Any?) {
        val mapper = ObjectMapper()
        val inStream = responseWrapper.contentInputStream
        val jsonObject = try {
            mapper.readValue<LinkedHashMap<String, Any?>>(inStream)
        } catch (ignored: JsonMappingException) {
            return
        }

        jsonObject[key] = value
        responseWrapper.reset()
        mapper.writeValue(responseWrapper.outputStream, jsonObject)
    }
}