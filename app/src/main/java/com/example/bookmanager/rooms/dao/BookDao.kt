package com.example.bookmanager.rooms.dao

import androidx.room.*
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

    @Query("SELECT * FROM books WHERE status = :status")
    suspend fun loadBooksByStatus(status: Int): List<Book>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun loadById(id: String): Book

    @Transaction
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun loadBookInfoById(id: String): BookInfo

    @Transaction
    @Query("SELECT * FROM books WHERE id IN (:ids)")
    suspend fun loadBookInfosByIds(ids: List<String>): List<BookInfo>

    @Query("SELECT status FROM books WHERE id = :id")
    suspend fun loadStatus(id: String): Int

    @Query("UPDATE books SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: String, rating: Int)

    @Query("UPDATE books SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: Int)

    @Query("UPDATE books SET started_at = :date WHERE id = :id")
    suspend fun updateStartDate(id: String, date: Long)

    @Query("UPDATE books SET started_at = 0 WHERE id = :id")
    suspend fun clearStartDate(id: String)

    @Query("UPDATE books SET finished_at = :date WHERE id = :id")
    suspend fun updateFinishDate(id: String, date: Long)

    @Query("UPDATE books SET started_at = 0 WHERE id = :id")
    suspend fun clearFinishDate(id: String)
}
