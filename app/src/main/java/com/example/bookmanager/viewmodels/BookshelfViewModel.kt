package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.bookmanager.models.BookSortCondition
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import kotlinx.coroutines.runBlocking

/**
 * 本棚に保存されている本の情報を保持するための ViewModel。
 */
class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val bookDao = Room.databaseBuilder(
        context, BookDatabase::class.java, C.DB_NAME
    ).build().bookDao()

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
            else -> return books
        }
    }

    private fun sortByTitle(books: List<Book>, isAsc: Boolean): List<Book> {
        return if (isAsc) {
            books.sortedBy { it.title }
        } else {
            books.sortedByDescending { it.title }
        }
    }

    private fun sortByAuthor(books: List<Book>, isAsc: Boolean): List<Book> {
        val ids = books.map { it.id }
        val bookInfoList = runBlocking { bookDao.loadBookInfosByIds(ids) }
        val sortedBookInfoList = if (isAsc) {
            bookInfoList.sortedBy { it.authors.first().name }
        } else {
            bookInfoList.sortedByDescending { it.authors.first().name }
        }
        return sortedBookInfoList.map { it.book }
    }

    private fun sortByDateAdded(books: List<Book>, newToOld: Boolean): List<Book> {
        return if (newToOld) {
            books.sortedByDescending { it.createdAt }
        } else {
            books.sortedBy { it.createdAt }
        }
    }
}
