package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.bookmanager.rooms.entities.AuthorBook

@Dao
interface AuthorBookDao {
    @Insert
    fun insert(authorBook: AuthorBook)
}
