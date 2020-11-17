package com.example.bookmanager.rooms.common

import android.content.Context
import androidx.room.Room
import androidx.room.Transaction
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.AuthorBook
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DaoController(private val context: Context) {

    private val db by lazy {
        Room.databaseBuilder(
            context, BookDatabase::class.java, C.DB_NAME
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
        val authorIds = insertOrGetIds(authors)
        val authorBooks = authorIds.map {
            AuthorBook.create(it, book.id)
        }
        authorBookDao.insertAll(authorBooks)
    }

    private suspend fun insertOrGetIds(authors: List<Author>): List<Long> {
        return authors.map { insertOrGetId(it) }
    }

    /**
     * DB にレコードが存在する場合は ID を返し、存在しない場合は保存したうえで ID を返す。
     */
    private suspend fun insertOrGetId(author: Author): Long {
        val insertedAuthor = withContext(Dispatchers.IO) {
            authorDao.loadByName(author.name)
        }

        return insertedAuthor?.id ?: authorDao.insert(author)
    }

    @Transaction
    suspend fun deleteBook(book: Book) = withContext(Dispatchers.IO) {
        // TODO: 著者が null の場合どうなる？
        val authorBooks = authorBookDao.getRecordsByBookId(book.id)
        val authorIds = authorBooks.map { it.authorId }
        // 中間テーブルからレコードを削除。
        for (data in authorBooks) {
            authorBookDao.delete(data)
        }
        // 本のレコードを削除。
        bookDao.deleteBooks(book)
        // 中間テーブルで使用されていない著者レコードは削除。
        for (id in authorIds) {
            if (authorBookDao.existsAuthor(id) == 0) {
                val author = authorDao.loadById(id)
                authorDao.delete(author)
            }
        }
    }
}
