package com.minakdan.bookmarks.internal.repository

import com.minakdan.bookmarks.api.Bookmark
import com.minakdan.bookmarks.jooq.tables.references.BOOKMARKS
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
internal class BookmarkRepository(
    private val dsl: DSLContext
) {
    fun create(id: UUID, url: String, title: String, description: String?, tags: List<String>): Bookmark {
        try {
            val record = dsl.insertInto(BOOKMARKS)
                .set(BOOKMARKS.ID, id)
                .set(BOOKMARKS.URL, url)
                .set(BOOKMARKS.TITLE, title)
                .set(BOOKMARKS.DESCRIPTION, description)
                .set(BOOKMARKS.TAGS, tags.toTypedArray())
                .returning()
                .fetchSingle()

            return record.toBookmark()
        }catch (_: DuplicateKeyException){
            throw IllegalArgumentException("Bookmark with title: $title already exists")
        }

    }

    fun getById(id: UUID): Bookmark {
        val record = dsl.selectFrom(BOOKMARKS)
            .where(BOOKMARKS.ID.eq(id))
            .fetchOne() ?: throw NoSuchElementException("Bookmark with id $id not found")
        return record.toBookmark()
    }

    fun update(
        id: UUID,
        url: String,
        title: String,
        description: String?,
        tags: List<String>,
        isFavorite: Boolean
    ): Bookmark {
        try{
            val updated = dsl.update(BOOKMARKS)
                .set(BOOKMARKS.URL, url)
                .set(BOOKMARKS.TITLE, title)
                .set(BOOKMARKS.DESCRIPTION, description)
                .set(BOOKMARKS.TAGS, tags.toTypedArray())
                .set(BOOKMARKS.IS_FAVORITE, isFavorite)
                .set(BOOKMARKS.UPDATED_AT, OffsetDateTime.now())
                .where(BOOKMARKS.ID.eq(id))
                .returning()
                .fetchOne() ?: throw NoSuchElementException("Bookmark with id $id not found")

            return updated.toBookmark()
        }catch(_: DuplicateKeyException){
            throw IllegalArgumentException("Bookmark with title: $title already exists")
        }
    }

    fun deleteById(id: UUID) {
        val rowsAffected = dsl.deleteFrom(BOOKMARKS)
            .where(BOOKMARKS.ID.eq(id))
            .execute()

        if (rowsAffected == 0) throw NoSuchElementException("Bookmark with id $id not found")
    }

    fun search(tag: String?, favorite: Boolean?, limit: Int, offset: Int): List<Bookmark> {
        var condition = DSL.noCondition()
        tag?.let { condition = condition.and(DSL.condition("tags @> ?::text[]", arrayOf(tag))) }
        favorite?.let { condition = condition.and(DSL.condition(BOOKMARKS.IS_FAVORITE.eq(favorite))) }

        return dsl.selectFrom(BOOKMARKS)
            .where(condition)
            .orderBy(BOOKMARKS.CREATED_AT.desc())
            .limit(limit)
            .offset(offset)
            .fetch()
            .map { it.toBookmark() }
    }

    fun findDistinctTags(): List<String> {
        return dsl.fetch("select distinct unnest(tags) as tag from bookmarks order by tag")
            .map { it.get("tag", String::class.java)}
    }

    fun setFavorite(id: UUID) {
        val rowsAffected = dsl.update(BOOKMARKS)
            .set(BOOKMARKS.IS_FAVORITE, true)
            .where(BOOKMARKS.ID.eq(id))
            .execute()
        if(rowsAffected == 0) throw NoSuchElementException("Bookmark with id $id not found")
    }


    private fun org.jooq.Record.toBookmark() = Bookmark(
        id = get(BOOKMARKS.ID)!!,
        url = get(BOOKMARKS.URL)!!,
        title = get(BOOKMARKS.TITLE)!!,
        description = get(BOOKMARKS.DESCRIPTION),
        tags = get(BOOKMARKS.TAGS)?.filterNotNull() ?: emptyList(),
        isFavorite = get(BOOKMARKS.IS_FAVORITE)!!,
        createdAt = get(BOOKMARKS.CREATED_AT)!!,
        updatedAt = get(BOOKMARKS.UPDATED_AT)!!
    )
}
