package com.minakdan.bookmarks.internal.controller

import com.minakdan.bookmarks.internal.dto.BookmarkCreateRequest
import com.minakdan.bookmarks.internal.dto.BookmarkResponse
import com.minakdan.bookmarks.internal.dto.BookmarkUpdateRequest
import com.minakdan.bookmarks.internal.dto.toResponse
import com.minakdan.bookmarks.internal.service.BookmarkService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/bookmarks")
internal class BookmarkController(
    private val service: BookmarkService
) {

    @PostMapping
    fun create(@Valid @RequestBody req: BookmarkCreateRequest): ResponseEntity<BookmarkResponse> {
        val created = service.create(req.url, req.title, req.description, req.tags)
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toResponse())
    }

    @PutMapping("/{id}")
    fun update(
        @Valid @RequestBody req: BookmarkUpdateRequest,
        @PathVariable id: UUID
    ): ResponseEntity<BookmarkResponse> {
        val updated = service.update(id, req.url, req.title, req.description, req.tags, req.isFavorite)
        return ResponseEntity.ok(updated.toResponse())
    }

    @PatchMapping("/favorite/{id}")
    fun setFavorite(@PathVariable id: UUID): ResponseEntity<Unit>{
        service.setFavorite(id)
        return ResponseEntity.ok(Unit)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<BookmarkResponse> =
        ResponseEntity.ok(service.getById(id).toResponse())

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: UUID): ResponseEntity<Void> {
        service.deleteById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @GetMapping
    fun getByParameters(
        @RequestParam tag: String?,
        @RequestParam favorite: Boolean?,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<List<BookmarkResponse>> =
        ResponseEntity.ok(service.search(tag, favorite, limit, offset).map { it.toResponse() })

    @GetMapping("/tags")
    fun getUniqueTags(): ResponseEntity<List<String>> =
        ResponseEntity.ok(service.getUniqueTags())

}
