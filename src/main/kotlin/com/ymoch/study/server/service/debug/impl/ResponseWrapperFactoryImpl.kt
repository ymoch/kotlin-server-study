package com.ymoch.study.server.service.debug.impl

import com.ymoch.study.server.service.debug.ResponseWrapperFactory
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletResponse

@Service
class ResponseWrapperFactoryImpl : ResponseWrapperFactory {
    override fun wrap(
            response: HttpServletResponse
    ): ContentCachingResponseWrapper = ContentCachingResponseWrapper(response)
}