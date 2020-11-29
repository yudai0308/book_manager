package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.rooms.entities.BookInfo

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book): Long

    @Delete
    suspend fun deleteBooks(vararg books: Book): Int

    @Query("SELECT COUNT(id) FROM books WHERE id = :id LIMIT 1")
    suspend fun exists(id: String): Int

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun load(id: String): Book

    @Query("SELECT * FROM books")
    suspend fun loadAll(): List<Book>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun loadById(id: String): Book

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun loadBookInfoById(id: String): BookInfo
}
