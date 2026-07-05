package com.minakdan.bookmarks

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<BookmarksApplication>().with(TestcontainersConfiguration::class).run(*args)
}
