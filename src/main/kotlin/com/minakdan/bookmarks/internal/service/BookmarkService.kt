package com.minakdan.bookmarks.internal.service

import com.minakdan.bookmarks.api.Bookmark
import com.minakdan.bookmarks.internal.repository.BookmarkRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
internal class BookmarkService(
    private val repository: BookmarkRepository
) {
    fun create(url: String, title: String, description: String?, tags: List<String>): Bookmark =
        repository.create(UUID.randomUUID(), url, title, description, tags)

    fun getById(id: UUID): Bookmark =
        repository.getById(id)

    fun update(id: UUID, url: String, title: String, description: String?, tags: List<String>, isFavorite: Boolean): Bookmark =
        repository.update(id, url, title, description, tags, isFavorite)

    fun deleteById(id: UUID) =
        repository.deleteById(id)

    fun search(tag: String?, favorite: Boolean?, limit: Int, offset: Int): List<Bookmark> =
        repository.search(tag, favorite, limit, offset)

    fun getUniqueTags(): List<String> =
        repository.findDistinctTags()

    fun setFavorite(id: UUID) =
        repository.setFavorite(id)
}
