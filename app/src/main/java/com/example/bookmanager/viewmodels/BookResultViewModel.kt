package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.models.BookSearchResultItem
import com.example.bookmanager.models.Item
import com.example.bookmanager.models.SearchResult
import com.example.bookmanager.rooms.common.BookRepository
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 本の検索結果を保持するための ViewModel。
 */
class BookResultViewModel(application: Application) : AndroidViewModel(application) {

    private val _bookSearchResult: MutableLiveData<BookSearchResult> = MutableLiveData()

    val bookSearchResult: LiveData<BookSearchResult> = _bookSearchResult

    private val context = application.applicationContext

    private var searchCallback: SearchCallback? = null

    private val repository by lazy { BookRepository(context) }

    companion object {
        const val ADD_QUERY = "?q="
        const val PARAM_TITLE = "intitle:"
        const val PARAM_AUTHOR = "inauthor:"
        const val PARAM_MAX = "&maxResults="
        const val PARAM_INDEX = "&startIndex="
        const val MAX_RESULTS_COUNT = 20
    }

    enum class SearchType {
        NEW,
        ADDITIONAL,
    }

    interface SearchCallback {
        fun onSearchStart()
        fun onSearchSucceeded(result: BookSearchResult)
        fun onSearchFailed()
    }

    fun searchBooks(
        query: String, searchWith: String, searchType: SearchType, callback: SearchCallback? = null
    ) {
        searchCallback = callback
        searchCallback?.onSearchStart()
        val index = when (searchType) {
            SearchType.NEW -> 0
            SearchType.ADDITIONAL -> _bookSearchResult.value?.items?.size ?: 0
        }
        val param = createUrlWithParameter(searchWith, query, MAX_RESULTS_COUNT, index)
        val url = C.BOOK_SEARCH_API_URL + param
        fetch(url, searchType)
    }

    private fun createUrlWithParameter(
        type: String, keyword: String, max: Int, index: Int = 0
    ): String {
        var param = ADD_QUERY
        param += when (type) {
            context.getString(R.string.search_with_title) -> PARAM_TITLE
            context.getString(R.string.search_with_author) -> PARAM_AUTHOR
            else -> ""
        }
        return param + keyword + PARAM_MAX + max + PARAM_INDEX + index
    }

    private fun fetch(url: String, searchType: SearchType) {
        val req = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder().build()
        val call = client.newCall(req)
        call.enqueue(BookSearchCallback(searchType))
    }

    inner class BookSearchCallback(private val searchType: SearchType) : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // TODO: 検索失敗時の処理
            searchCallback?.onSearchFailed()
        }

        override fun onResponse(call: Call, response: Response) {
            val emptyBookSearchResult = BookSearchResult(0, listOf())
            val emptySearchResult = SearchResult(0, listOf())

            val resBody = response.body?.string()
            if (resBody.isNullOrBlank()) {
                searchCallback?.onSearchSucceeded(emptyBookSearchResult)
                return
            }
            val adapter = Moshi.Builder().build().adapter(SearchResult::class.java)
            val originalResult = adapter.fromJson(resBody) ?: emptySearchResult
            var resultItems = if (originalResult.items == null) {
                listOf()
            } else {
                createResultItems(originalResult.items as List<Item>)
            }
            if (searchType == SearchType.ADDITIONAL) {
                val currentItems = _bookSearchResult.value?.items ?: listOf()
                resultItems = currentItems + resultItems
            }
            val bookSearchResult = BookSearchResult(originalResult.totalItems, resultItems)
            _bookSearchResult.postValue(bookSearchResult)
            searchCallback?.onSearchSucceeded(bookSearchResult)
        }
    }

    private fun createResultItems(items: List<Item>): List<BookSearchResultItem> {
        val books = mutableListOf<BookSearchResultItem>()
        for (item in items) {
            val info = item.volumeInfo ?: continue
            val id = item.id
            val title = info.title ?: continue
            val authors = info.authors ?: listOf(context.getString(R.string.hyphen))
            val publishedDate = info.publishedDate?.let {
                getDateTimeFromDateString(it)
            } ?: 0
            val averageRating = info.averageRating
            val ratingsCount = info.ratingsCount ?: 0
            val description = info.description ?: ""
            val image = info.imageLinks?.thumbnail ?: ""
            val infoLink = info.infoLink ?: ""
            val isAlreadyAdded = repository.exists(id)
            books.add(
                BookSearchResultItem(
                    id, title, authors, publishedDate, averageRating,
                    ratingsCount, description, image, infoLink, isAlreadyAdded
                )
            )
        }
        return books
    }

    private fun getDateTimeFromDateString(dateString: String): Long {
        val formattedDateString = createFormattedDateString(dateString)
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN).parse(formattedDateString)
            date?.time ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun createFormattedDateString(dateString: String): String {
        val dateArray = dateString.split("-")
        return when (dateArray.size) {
            1 -> "${dateArray[0]}-01-01"
            2 -> "${dateArray[0]}-${dateArray[1]}-01"
            3 -> dateString
            else -> "1970-01-01"
        }
    }

    fun saveBook(item: BookSearchResultItem) {
        saveBookToDb(item)
        updateIsAlreadyAdded(item.id, true)
    }

    private fun saveBookToDb(item: BookSearchResultItem) {
        if (repository.exists(item.id)) {
            return
        }

        val newBook = Book.create(
            item.id,
            item.title,
            item.description,
            item.image,
            item.infoLink,
            item.publishedDate
        )
        val authors = Author.createAll(item.authors)
        GlobalScope.launch {
            repository.insertBookWithAuthors(newBook, authors)
        }
        if (newBook.image.isNotBlank()) {
            saveImageToInternalStorage(newBook.image, newBook.id)
        }
    }

    private fun updateIsAlreadyAdded(id: String, flag: Boolean) {
        _bookSearchResult.value?.apply {
            items.forEach {
                if (it.id == id) {
                    it.isAlreadyAdded = flag
                }
            }
        }
    }

    private fun saveImageToInternalStorage(url: String, fileName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = Glide.with(context).asBitmap().load(url).submit().get()
            FileIO.saveBookImage(context, bitmap, fileName)
        }
    }
}
