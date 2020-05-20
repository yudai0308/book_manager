package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookmanager.rooms.entities.Book

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book)
    @Query("SELECT COUNT(id) FROM books WHERE id = :id")
    suspend fun count(id: String) : Int
}
