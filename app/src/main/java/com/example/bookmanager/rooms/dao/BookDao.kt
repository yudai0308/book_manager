package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.rooms.entities.BookInfo

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book)

    @Query("SELECT COUNT(id) FROM books WHERE id = :id LIMIT 1")
    suspend fun exists(id: String): Int

    @Transaction
    @Query("SELECT * FROM books")
    suspend fun loadBookInfo(): List<BookInfo>
}
