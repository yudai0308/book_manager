package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.bookmanager.R
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.rooms.entities.BookInfo
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.util.*

/**
 * 本のレビュー内容を保持するための ViewModel。
 */
class BookInfoViewModel(application: Application, val bookId: String) :
    AndroidViewModel(application) {

    private val context = application.applicationContext

    //    private val bookId = bookId

    private val bookDao by lazy {
        Room.databaseBuilder(context, BookDatabase::class.java, C.DB_NAME).build().bookDao()
    }

    private val bookInfo: BookInfo by lazy {
        runBlocking { bookDao.loadBookInfoById(bookId) }
    }

    val statusStr = MutableLiveData<String>()

    val startDateStr = MutableLiveData<String>()

    val finishDateStr = MutableLiveData<String>()

    var review: String = ""

    init {
        statusStr.value = getStatusString(bookInfo.book.status)
        startDateStr.value = if (bookInfo.book.startDate > 0) {
            val date = Date(bookInfo.book.startDate * 1000)
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN).format(date)
        } else {
            ""
        }
        finishDateStr.value = if (bookInfo.book.finishDate > 0) {
            val date = Date(bookInfo.book.finishDate * 1000)
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN).format(date)
        } else {
            ""
        }
    }

    private fun getStatusString(statusCode: Int): String {
        return when (statusCode) {
            Book.Status.PLANNING.code -> context.getString(R.string.book_status_want_to)
            Book.Status.READING.code -> context.getString(R.string.book_status_reading)
            Book.Status.FINISHED.code -> context.getString(R.string.book_status_finished)
            else -> ""
        }
    }

    fun fetchCurrentStatus(): Int = runBlocking {
        bookDao.loadStatus(bookId)
    }

    fun updateStatus(statusCode: Int) {
        statusStr.value = getStatusString(statusCode)
        GlobalScope.launch {
            bookDao.updateStatus(bookId, statusCode)
        }
    }

    fun updateStartDate(dateStr: String) {
        startDateStr.value = dateStr
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN)
        val date = dateFormat.parse(dateStr) ?: return
        val time = date.time / 1000
        GlobalScope.launch {
            bookDao.updateStartDate(bookId, time)
        }
    }

    fun clearStartDate() {
        startDateStr.value = ""
        GlobalScope.launch {
            bookDao.clearStartDate(bookId)
        }
    }

    fun updateFinishDate(dateStr: String) {
        finishDateStr.value = dateStr
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN)
        val date = dateFormat.parse(dateStr) ?: return
        val time = date.time / 1000
        GlobalScope.launch {
            bookDao.updateFinishDate(bookId, time)
        }
    }

    fun clearFinishDate() {
        finishDateStr.value = ""
        GlobalScope.launch {
            bookDao.clearFinishDate(bookId)
        }
    }

    fun readReviewContent(bookId: String) {
        review = FileIO.readReviewFile(context, bookId) ?: return
    }

    fun getDescription() = bookInfo.book.description

    /**
     * Book ID を引数として受け取るために、独自の Factory クラスを作成。
     */
    class Factory(private val application: Application, private val bookId: String) :
        ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BookInfoViewModel(application, bookId) as T
        }
    }
}

