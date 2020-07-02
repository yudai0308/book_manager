package com.example.bookmanager.rooms.common

import android.content.Context
import androidx.room.Room
import androidx.room.Transaction
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.AuthorBook
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.Const

class DaoController(private val context: Context) {

    private val db by lazy {
        Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            Const.DB_NAME
        ).build()
    }

    private val bookDao by lazy { db.bookDao() }

    private val authorDao by lazy { db.authorDao() }

    private val authorBookDao by lazy { db.authorBookDao() }

    // TODO: トランザクションが正常に動作しているか確認。
    @Transaction
    suspend fun insertBookWithAuthors(book: Book, authors: List<Author>) {
        bookDao.insert(book)
        val authorIds = authorDao.insertAll(authors)
        val authorBooks = authorIds.map {
            AuthorBook.create(it, book.id)
        }
        authorBookDao.insertAll(authorBooks)
    }
}
