package com.example.bookmanager.rooms.entities

import androidx.room.*

@Entity(
    tableName = "authors_books",
    foreignKeys = [
        ForeignKey(
            entity = Author::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("author_id")
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("book_id")
        )
    ],
    indices = [
        Index(value = ["author_id"], unique = true),
        Index(value = ["book_id"], unique = true)
    ]
)
data class AuthorBook(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo(name = "book_id") val bookId: String
)
