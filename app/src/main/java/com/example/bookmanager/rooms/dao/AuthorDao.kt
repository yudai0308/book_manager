package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.bookmanager.rooms.entities.Author

@Dao
interface AuthorDao {
    @Insert
    fun insert(author: Author)
}
