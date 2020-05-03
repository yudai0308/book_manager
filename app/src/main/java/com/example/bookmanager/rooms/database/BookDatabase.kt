package com.example.bookmanager.rooms.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookmanager.models.Book
import com.example.bookmanager.rooms.dao.BookDao

@Database(entities = [Book::class], version = 1)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
