package com.example.bookmanager.rooms.common

import android.content.Context
import androidx.room.Room
import androidx.room.Transaction
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.AuthorBook
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.Const
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    // TODO: 著者がすでに登録されていたら中間テーブルにのみ登録。
    @Transaction
    suspend fun insertBookWithAuthors(book: Book, authors: List<Author>) {
        // TODO: 著者名が登録済みなら登録しない（登録しようとするとユニーク制約エラー発生）
        bookDao.insert(book)
        val authorIds = insertOrGetIdAll(authors)
        val authorBooks = authorIds.map {
            AuthorBook.create(it, book.id)
        }
        authorBookDao.insertAll(authorBooks)
    }

    private suspend fun insertOrGetIdAll(authors: List<Author>): List<Long> {
        return authors.map { insertOrGetId(it) }
    }

    /**
     * DB にレコードが存在する場合は ID を返し、存在しない場合は保存したうえで ID を返す。
     */
    private suspend fun insertOrGetId(author: Author): Long {
        val insertedAuthor = withContext(Dispatchers.Default) {
            authorDao.loadByName(author.name)
        }

        return insertedAuthor?.id ?: authorDao.insert(author)

    }
}
