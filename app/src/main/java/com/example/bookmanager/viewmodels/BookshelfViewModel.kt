package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.Const
import kotlinx.coroutines.runBlocking

class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val bookDao = Room.databaseBuilder(
        context,
        BookDatabase::class.java,
        Const.DB_NAME
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
