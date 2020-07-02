package com.example.bookmanager.rooms.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class BookInfo(
    @Embedded var book: Book = Book.create("", "", ""),
    @Relation(
        entity = Author::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AuthorBook::class,
            parentColumn = "book_id",
            entityColumn = "author_id"
        )
    )
    var authors: List<Author> = arrayListOf()
)
