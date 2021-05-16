package io.github.yudai0308.honma.rooms.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class BookInfo(
    @Embedded val book: Book,
    @Relation(
        entity = Author::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AuthorBook::class,
            parentColumn = "book_id",
            entityColumn = "author_id"
        )
    ) val authors: List<Author>
)
