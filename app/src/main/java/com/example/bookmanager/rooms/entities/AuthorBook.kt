package com.example.bookmanager.rooms.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors_books",
    indices = [
        // author_id と book_id の組み合わせを重複させない。
        Index(value = ["author_id", "book_id"], unique = true)
    ]
)
data class AuthorBook(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "author_id") val authorId: Long,
    @ColumnInfo(name = "book_id") val bookId: String
) {
    companion object {
        fun create(authorId: Long, bookId: String): AuthorBook {
            return AuthorBook(0, authorId, bookId)
        }
    }
}
