package com.minakdan.bookmarks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookmarksApplication

fun main(args: Array<String>) {
    runApplication<BookmarksApplication>(*args)
}
