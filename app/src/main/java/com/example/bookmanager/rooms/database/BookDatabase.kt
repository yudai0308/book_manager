package com.example.bookmanager.rooms.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookmanager.rooms.dao.AuthorBookDao
import com.example.bookmanager.rooms.dao.AuthorDao
import com.example.bookmanager.rooms.dao.BookDao
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.AuthorBook
import com.example.bookmanager.rooms.entities.Book
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [Book::class, Author::class, AuthorBook::class],
    version = 1
)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun authorDao(): AuthorDao
    abstract fun authorBookDao(): AuthorBookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        @InternalCoroutinesApi
        fun getDatabase(context: Context): BookDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
