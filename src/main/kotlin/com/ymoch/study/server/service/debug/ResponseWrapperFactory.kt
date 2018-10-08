package com.ymoch.study.server.service.debug

import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletResponse

interface ResponseWrapperFactory {
    fun wrap(response: HttpServletResponse): ContentCachingResponseWrapper
}