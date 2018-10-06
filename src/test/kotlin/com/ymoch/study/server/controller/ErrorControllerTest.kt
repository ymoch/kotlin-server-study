package com.ymoch.study.server.controller

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
internal class ErrorControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    inner class ErrorRaiseTest {
        @Test
        fun raisesApplicationRuntimeException() {
            mockMvc.perform(MockMvcRequestBuilders.get("/error/raise"))
                    .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class ErrorRaiseUnexpectedTest {
        @Test
        fun raisesRuntimeException() {
            mockMvc.perform(MockMvcRequestBuilders.get("/error/raise/unexpected"))
                    .andExpect(status().isInternalServerError)
        }
    }
}