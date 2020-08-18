package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import kotlinx.coroutines.runBlocking

/**
 * 本棚に保存されている本を保持するための ViewModel。
 */
class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val bookDao = Room.databaseBuilder(
        context,
        BookDatabase::class.java,
        C.DB_NAME
    ).build().bookDao()

    private val _books: MutableLiveData<List<Book>> = MutableLiveData()

    val books: LiveData<List<Book>> = _books

    fun reload() {
        _books.postValue(
            // TODO: runBlocking がベストか？
            runBlocking { bookDao.loadAll() }
        )
    }
}
