package com.minakdan.bookmarks.internal.controller

import com.minakdan.bookmarks.api.Bookmark
import com.minakdan.bookmarks.internal.dto.BookmarkCreateRequest
import com.minakdan.bookmarks.internal.service.BookmarkService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime
import java.util.UUID

@TestConfiguration
internal class BookmarkControllerTestConfig{
    @Bean
    fun bookmarkService(): BookmarkService = mockk()
}

@WebMvcTest(BookmarkController::class)
@Import(BookmarkControllerTestConfig::class)
class BookmarkControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var service: BookmarkService

    @BeforeEach
    fun resetMocks(){
        clearMocks(service)
    }

    private var testBookmark = Bookmark(
        id = UUID.randomUUID(),
        url = "url",
        title = "title",
        description = "some description",
        tags = listOf("kotlin", "jooq"),
        isFavorite = true,
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
    )


    @Nested
    inner class Create{

        @Test
        fun `create bookmark and returns it`(){
            every { service.create("url", "title", "some description", listOf("kotlin", "jooq")) } returns testBookmark

            val request = BookmarkCreateRequest("url", "title", "some description", listOf("kotlin", "jooq"))

            mockMvc.perform(
                post("/api/v1/bookmarks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect { status().isCreated }
                .andExpect { jsonPath("$.title").value("title") }
                .andExpect { jsonPath("$.url").value("url") }
        }

        @Test
        fun `create bookmark without url`(){
            val request = BookmarkCreateRequest("", "title", "some description", listOf("kotlin", "jooq"))

            mockMvc.perform(
                post("/api/v1/bookmarks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect { status().isBadRequest }
                .andExpect { jsonPath("$.url").exists() }
        }
    }
}