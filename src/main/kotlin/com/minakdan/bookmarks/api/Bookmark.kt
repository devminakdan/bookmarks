package com.minakdan.bookmarks.api

import java.time.OffsetDateTime
import java.util.UUID

data class Bookmark(
    val id: UUID,
    val url: String,
    val title: String,
    val description: String?,
    val tags: List<String>,
    val isFavorite: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
