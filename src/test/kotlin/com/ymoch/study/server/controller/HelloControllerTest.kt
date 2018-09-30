package com.ymoch.study.server.controller

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
internal class HelloControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    inner class SayHelloToTest {

        @Nested
        inner class WhenGivenNoTarget {
            @Test
            fun thenSayHelloToTheWorld() {
                val request = MockMvcRequestBuilders.get("/hello/world")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                mockMvc.perform(request)
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.message").value("Hello, world."))
                        .andExpect(jsonPath("$.target").value("world"))
            }
        }

        @Nested
        inner class WhenGivenATarget {
            @Test
            fun thenSayHelloToTheTarget() {
                val request = MockMvcRequestBuilders.get("/hello/world")
                        .param("target", "you")
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                mockMvc.perform(request)
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.message").value("Hello, you."))
                        .andExpect(jsonPath("$.target").value("you"))
            }
        }
    }
}