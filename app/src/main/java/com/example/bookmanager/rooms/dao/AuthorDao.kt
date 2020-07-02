package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.bookmanager.rooms.entities.Author

@Dao
interface AuthorDao {
    @Insert
    suspend fun insert(author: Author)

    @Insert
    suspend fun insertAll(authors: List<Author>): List<Long>
}
