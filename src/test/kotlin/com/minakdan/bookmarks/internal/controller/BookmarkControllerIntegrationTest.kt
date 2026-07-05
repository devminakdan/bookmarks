package com.minakdan.bookmarks.internal.controller

import com.minakdan.bookmarks.TestcontainersConfiguration
import com.minakdan.bookmarks.internal.dto.BookmarkCreateRequest
import com.minakdan.bookmarks.internal.dto.BookmarkResponse
import com.minakdan.bookmarks.jooq.tables.references.BOOKMARKS
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.everyItem
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasSize
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.util.UUID
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
class BookmarkControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var dsl: DSLContext

    @BeforeEach
    fun cleanDatabase() {
        dsl.deleteFrom(BOOKMARKS).execute()
    }

    private fun createBookmarks(){
        for (i in 0..5){
            var req = BookmarkCreateRequest("url$i", "title$i")
            req = when(i % 2 == 0){
                true -> req.copy(tags = listOf("kotlin", "jooq"))
                else -> req.copy(tags = listOf("java", "jpa"))
            }
            mockMvc.perform(
                post("/api/v1/bookmarks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                .andExpect( status().isCreated )
        }
    }

    @Test
    fun `create bookmarks and count it after`(){
        createBookmarks()
        mockMvc.perform(get("/api/v1/bookmarks"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(6))
    }
    

    @Test
    fun `list all bookmarks that have kotlin tag`(){
        createBookmarks()
        mockMvc.perform(get("/api/v1/bookmarks").param("tag", "kotlin"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect ( jsonPath("$.[*].tags", everyItem(hasItem("kotlin"))) )
    }

    @Test
    fun `list all bookmarks that are favorite`(){
        createBookmarks()
        val body = mockMvc.perform(get("/api/v1/bookmarks").param("tag", "kotlin"))
            .andExpect ( status().isOk )
            .andReturn()
            .response
            .contentAsString
        val ids = objectMapper.readValue(body, Array<BookmarkResponse>::class.java).map { it.id }
        for(bookmarkId in ids){
            mockMvc.perform(patch("/api/v1/bookmarks/favorite/{id}", bookmarkId))
                .andExpect ( status().isOk )
        }

        mockMvc.perform(get("/api/v1/bookmarks").param("favorite", true.toString()))
            .andExpect ( status().isOk )
            .andExpect ( jsonPath("$.length()").value(3))
            .andExpect ( jsonPath("$[*].tags", allOf(
                everyItem(hasItem("kotlin")),
                everyItem(hasItem("jooq"))
            )))
    }

    @Test
    fun `get by id non exists`(){
        mockMvc.perform(get("/api/v1/bookmarks/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get all unique tags`() {
        createBookmarks()
        mockMvc.perform(get("/api/v1/bookmarks/tags"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", containsInAnyOrder("java", "kotlin", "jpa","jooq")))
    }


    // TODO: create -> 201, list (без фильтров / tag / favorite / комбинация), getById (200/404), update, delete (204 -> 404), /tags (с данными / пустая БД)
}
