package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C

/**
 * 本棚に保存されている本を保持するための ViewModel。
 */
class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val bookDao = Room.databaseBuilder(
        context, BookDatabase::class.java, C.DB_NAME
    ).build().bookDao()

    private val _books: MutableLiveData<List<Book>> = MutableLiveData()

    val books: LiveData<List<Book>> = _books

    suspend fun fetchAllBooks() {
        _books.postValue(bookDao.loadAll())
    }

    suspend fun fetchBooksWantToRead() {
        _books.postValue(bookDao.loadBooksByStatus(0))
    }

    suspend fun fetchBooksReading() {
        _books.postValue(bookDao.loadBooksByStatus(1))
    }

    suspend fun fetchBooksFinished() {
        _books.postValue(bookDao.loadBooksByStatus(2))
    }
}
