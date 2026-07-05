package com.minakdan.bookmarks.internal.service

import com.minakdan.bookmarks.api.Bookmark
import com.minakdan.bookmarks.internal.repository.BookmarkRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class BookmarkServiceTest {
    @MockK
    private lateinit var repository: BookmarkRepository

    @InjectMockKs
    private lateinit var service: BookmarkService

    private val testBookmark = Bookmark(
        id = UUID.randomUUID(),
        url = "url_string",
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
        fun `create bookmark returns new bookmark`(){
            every{
                repository.create(any(), "url_string", "title", "some description", listOf("kotlin", "jooq"))
            } returns testBookmark

            val result = service.create("url_string", "title", "some description", listOf("kotlin", "jooq"))

            assertEquals("url_string", result.url)
            assertEquals("title", result.title)
            verify(exactly = 1){ repository.create(any(), any(), any(), any(), any())}
        }

        @Test
        fun `create bookmark with duplicate title throws`() {
            every {
                repository.create(any(), any(), eq("title"), any(), any())
            } throws IllegalArgumentException("Bookmark with title 'title' already exists")

            assertFailsWith<IllegalArgumentException> {
                service.create("url", "title", null, emptyList())
            }
        }
    }
}