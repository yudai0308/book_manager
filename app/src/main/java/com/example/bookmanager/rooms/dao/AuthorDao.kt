package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookmanager.rooms.entities.Author

@Dao
interface AuthorDao {
    @Insert
    suspend fun insert(author: Author): Long

    @Insert
    suspend fun insertAll(authors: List<Author>): List<Long>

    @Query("SELECT * FROM authors WHERE name = :name LIMIT 1")
    suspend fun loadByName(name: String): Author?
}
