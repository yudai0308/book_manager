package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bookmanager.rooms.entities.AuthorBook

@Dao
interface AuthorBookDao {
    @Insert
    suspend fun insert(authorBook: AuthorBook)

    @Insert
    suspend fun insertAll(authorBooks: List<AuthorBook>)

    @Delete
    suspend fun delete(authorBook: AuthorBook)

    @Query("SELECT * FROM authors_books WHERE book_id = :id")
    suspend fun getRecordsByBookId(id: String): List<AuthorBook>

    @Query("SELECT COUNT(*) FROM authors_books WHERE author_id = :authorId LIMIT 1")
    suspend fun existsAuthor(authorId: Long): Int
}
