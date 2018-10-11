package com.ymoch.study.server.service.debug

import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletResponse

@Service
internal class ResponseWrapperFactoryImpl : ResponseWrapperFactory {
    override fun wrap(
            response: HttpServletResponse
    ): ContentCachingResponseWrapper = ContentCachingResponseWrapper(response)
}