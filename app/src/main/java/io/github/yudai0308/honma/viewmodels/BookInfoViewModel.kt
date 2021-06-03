package io.github.yudai0308.honma.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import io.github.yudai0308.honma.rooms.common.BookRepository
import io.github.yudai0308.honma.rooms.database.BookDatabase
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.rooms.entities.BookInfo
import io.github.yudai0308.honma.utils.C
import io.github.yudai0308.honma.utils.FileIO
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

    private val bookDao by lazy {
        Room.databaseBuilder(context, BookDatabase::class.java, C.DB_NAME).build().bookDao()
    }

    private val repository by lazy { BookRepository(context) }


    private val bookInfo: BookInfo by lazy {
        runBlocking { bookDao.loadBookInfoById(bookId) }
    }

    val rating = MutableLiveData<Int>()

    val statusCode = MutableLiveData<Int>()

    val startDateStr = MutableLiveData<String>()

    val finishDateStr = MutableLiveData<String>()

    var review: String = ""

    init {
        rating.value = bookInfo.book.rating
        statusCode.value = getCurrentStatus().code
        startDateStr.value = if (bookInfo.book.startedAt > 0) {
            val date = Date(bookInfo.book.startedAt * 1000)
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN).format(date)
        } else {
            ""
        }
        finishDateStr.value = if (bookInfo.book.finishedAt > 0) {
            val date = Date(bookInfo.book.finishedAt * 1000)
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN).format(date)
        } else {
            ""
        }
    }

    private fun getCurrentStatus(): Book.Status {
        return when (bookInfo.book.status) {
            Book.Status.WANT_TO_READ.code -> Book.Status.WANT_TO_READ
            Book.Status.READING.code -> Book.Status.READING
            Book.Status.FINISHED.code -> Book.Status.FINISHED
            else -> throw Exception("Detect invalid book status.")
        }
    }

    fun updateRating(rating: Int) {
        this.rating.value = rating
        GlobalScope.launch {
            bookDao.updateRating(bookId, rating)
        }
    }

    fun updateStatus(status: Book.Status) {
        statusCode.value = status.code
        GlobalScope.launch {
            bookDao.updateStatus(bookId, status.code)
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

    suspend fun delete(context: Context, book: Book) {
        repository.delete(book)
        FileIO.deleteBookImage(context, book.id)
        FileIO.deleteBookReviewFile(context, book.id)
    }

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

