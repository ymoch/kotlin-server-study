package com.ymoch.study.server.service.debug.impl

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.servlet.http.HttpServletResponse

internal class ResponseWrapperFactoryImplTest {

    @Test
    fun wrapsResponse() {
        val response = Mockito.mock(HttpServletResponse::class.java)

        val wrappedResponse = ResponseWrapperFactoryImpl().wrap(response)
        val innerResponse = wrappedResponse.response
        MatcherAssert.assertThat(
                innerResponse as HttpServletResponse, Matchers.equalTo(response))
    }
}