package io.github.yudai0308.honma.rooms.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.yudai0308.honma.rooms.dao.AuthorBookDao
import io.github.yudai0308.honma.rooms.dao.AuthorDao
import io.github.yudai0308.honma.rooms.dao.BookDao
import io.github.yudai0308.honma.rooms.entities.Author
import io.github.yudai0308.honma.rooms.entities.AuthorBook
import io.github.yudai0308.honma.rooms.entities.Book
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [Book::class, Author::class, AuthorBook::class],
    version = 1,
    exportSchema = false
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
