package com.minakdan.bookmarks.internal.dto

import com.minakdan.bookmarks.api.Bookmark
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

internal data class BookmarkCreateRequest(
    @field:NotBlank
    @field:Size(max = 2000, message = "Url must be at most 2000 characters")
    val url: String,

    @field:NotBlank
    @field:Size(max = 300, message = "Title must be at most 300 characters")
    val title: String,

    val description: String? = null,

    val tags: List<String> = emptyList()
)

internal data class BookmarkUpdateRequest(
    @field:NotBlank
    @field:Size(max = 2000, message = "Url must be at most 2000 characters")
    val url: String,

    @field:NotBlank
    @field:Size(max = 300, message = "Title must be at most 300 characters")
    val title: String,

    val description: String?,

    val tags: List<String>,

    val isFavorite: Boolean
)

internal data class BookmarkResponse(
    val id: UUID,
    val url: String,
    val title: String,
    val description: String?,
    val tags: List<String>,
    val isFavorite: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

internal fun Bookmark.toResponse() = BookmarkResponse(
    id = id,
    url = url,
    title = title,
    description = description,
    tags = tags,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)
