package io.github.yudai0308.honma.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import io.github.yudai0308.honma.R
import io.github.yudai0308.honma.models.BookSortCondition
import io.github.yudai0308.honma.rooms.common.BookRepository
import io.github.yudai0308.honma.rooms.database.BookDatabase
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.utils.C
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * 本棚に保存されている本の情報を保持するための ViewModel。
 */
class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val bookDao = Room.databaseBuilder(
        context, BookDatabase::class.java, C.DB_NAME
    ).build().bookDao()

    private val repository by lazy { BookRepository(context) }

    private val _books: MutableLiveData<List<Book>> = MutableLiveData()

    val books: LiveData<List<Book>> = _books

    suspend fun fetchBooks(status: Book.Status?, condition: BookSortCondition): List<Book> {
        val books = if (status == null) {
            bookDao.loadAll()
        } else {
            bookDao.loadBooksByStatus(status.code)
        }
        val sortedBooks = getSortedBooks(books, condition)
        _books.postValue(sortedBooks)
        return books
    }

    private fun getSortedBooks(books: List<Book>, condition: BookSortCondition): List<Book> {
        return when (condition.column) {
            Book.Column.TITLE -> sortByTitle(books, condition.isAsc)
            Book.Column.AUTHOR -> sortByAuthor(books, condition.isAsc)
            Book.Column.CREATED_AT -> sortByDateAdded(books, condition.isAsc)
            Book.Column.RATING -> sortByRating(books, condition.isAsc)
        }
    }

    private fun sortByTitle(books: List<Book>, isAsc: Boolean): List<Book> {
        val groupedBooksList = books.groupBy { it.seriesName }
        val seriesNames = groupedBooksList.keys
        val sortedSeriesNames = if (isAsc) {
            seriesNames.sorted()
        } else {
            seriesNames.sortedDescending()
        }
        var sortedBooks = listOf<Book>()
        sortedSeriesNames.forEach { series ->
            val seriesBooks = groupedBooksList[series] ?: return@forEach
            sortedBooks = sortedBooks + sortByPublishedDate(seriesBooks, isAsc)
        }

        return sortedBooks
    }

    private fun sortByAuthor(books: List<Book>, isAsc: Boolean): List<Book> {
        val noAuthorString = context.getString(R.string.hyphen)
        val ids = books.map { it.id }
        val bookInfoList = runBlocking { bookDao.loadBookInfosByIds(ids) }
        val infoListHaveAuthor = bookInfoList.filter { it.authors.first().name != noAuthorString }
        val infoListHaveNoAuthor = bookInfoList.filter { it.authors.first().name == noAuthorString }
        val groupedBookInfoList = infoListHaveAuthor.groupBy { it.authors.first().name }.let {
            if (isAsc) {
                it.toSortedMap()
            } else {
                it.toSortedMap(Comparator.reverseOrder())
            }
        }
        var sortedBooks = listOf<Book>()
        groupedBookInfoList.forEach { (_, infoList) ->
            sortedBooks = sortedBooks + sortByTitle(infoList.map { it.book }, true)
        }

        return sortedBooks + sortByTitle(infoListHaveNoAuthor.map { it.book }, true)
    }

    private fun sortByDateAdded(books: List<Book>, oldToNew: Boolean): List<Book> {
        return if (oldToNew) {
            books.sortedBy { it.createdAt }
        } else {
            books.sortedByDescending { it.createdAt }
        }
    }

    private fun sortByRating(books: List<Book>, isAsc: Boolean): List<Book> {
        val booksHaveNoRating = books.filter { it.rating == 0 }
        val booksHaveRating = books.filter { it.rating > 0 }
        val groupedBooks = booksHaveRating.groupBy { it.rating }.let {
            if (isAsc) {
                it.toSortedMap()
            } else {
                it.toSortedMap(reverseOrder())
            }
        }
        var sortedBooks = listOf<Book>()
        groupedBooks.forEach { (_, books) ->
            sortedBooks = sortedBooks + sortByTitle(books, true)
        }

        return sortedBooks + sortByTitle(booksHaveNoRating, true)
    }

    private fun sortByPublishedDate(books: List<Book>, isAsk: Boolean): List<Book> {
        return if (isAsk) {
            books.sortedBy { it.publishedDate }
        } else {
            books.sortedByDescending { it.publishedDate }
        }
    }

    suspend fun delete(book: Book) {
        repository.delete(book)
    }
}
