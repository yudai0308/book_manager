package io.github.yudai0308.honma.rooms.common

import android.content.Context
import androidx.room.Room
import androidx.room.Transaction
import io.github.yudai0308.honma.rooms.database.BookDatabase
import io.github.yudai0308.honma.rooms.entities.Author
import io.github.yudai0308.honma.rooms.entities.AuthorBook
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.utils.C
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BookRepository(private val context: Context) {

    private val db by lazy {
        Room.databaseBuilder(
            context, BookDatabase::class.java, C.DB_NAME
        ).build()
    }

    private val bookDao by lazy { db.bookDao() }

    private val authorDao by lazy { db.authorDao() }

    private val authorBookDao by lazy { db.authorBookDao() }

    @Transaction
    suspend fun insertBookWithAuthors(book: Book, authors: List<Author>) {
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
    suspend fun delete(book: Book) = withContext(Dispatchers.IO) {
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

    fun exists(id: String): Boolean {
        return runBlocking { bookDao.exists(id) } > 0
    }
}
