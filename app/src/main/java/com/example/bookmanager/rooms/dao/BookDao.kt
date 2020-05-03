package com.example.bookmanager.rooms.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.bookmanager.rooms.entities.Book

@Dao
interface BookDao {
    @Insert
    fun insert(book: Book)
}
