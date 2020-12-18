package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookmanager.R
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.models.Item
import com.example.bookmanager.models.SearchResult
import com.example.bookmanager.utils.C
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException

/**
 * 本の検索結果を保持するための ViewModel。
 */
class BookResultViewModel(application: Application) : AndroidViewModel(application) {

    private val _resultBooks: MutableLiveData<List<BookSearchResult>> = MutableLiveData()

    val resultBooks: LiveData<List<BookSearchResult>> = _resultBooks

    private val context = application.applicationContext

    private var searchCallback: SearchCallback? = null

    enum class SearchType {
        NEW,
        ADDITIONAL,
    }

    interface SearchCallback {
        fun onSearchStart()
        fun onSearchSucceeded(resultBooks: List<BookSearchResult>)
        fun onSearchFailed()
    }

    fun searchBooks(
        query: String, searchWith: String, searchType: SearchType, callback: SearchCallback? = null
    ) {
        searchCallback = callback
        searchCallback?.onSearchStart()
        val index = when (searchType) {
            SearchType.NEW -> 0
            SearchType.ADDITIONAL -> _resultBooks.value?.size ?: 0
        }
        val param = createUrlWithParameter(searchWith, query, C.MAX_RESULTS_COUNT, index)
        val url = C.BOOK_SEARCH_API_URL + param
        fetch(url, searchType)
    }

    private fun createUrlWithParameter(
        type: String, keyword: String, max: Int, index: Int = 0
    ): String {
        var param = C.ADD_QUERY
        param += when (type) {
            C.SEARCH_TITLE -> C.PARAM_TITLE
            C.SEARCH_AUTHOR -> C.PARAM_AUTHOR
            else -> ""
        }
        return param + keyword + C.PARAM_MAX + max + C.PARAM_INDEX + index
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
            val resBody = response.body?.string()
            if (resBody.isNullOrBlank()) {
                return
            }
            val adapter = Moshi.Builder().build().adapter(SearchResult::class.java)
            val result = adapter.fromJson(resBody) ?: return
            var resultBooks = if (result.items == null) {
                listOf()
            } else {
                createResultBooks(result.items as List<Item>)
            }
            if (searchType == SearchType.ADDITIONAL) {
                val currentBooks = _resultBooks.value ?: return
                resultBooks = currentBooks.plus(resultBooks)
            }
            _resultBooks.postValue(resultBooks)
            searchCallback?.onSearchSucceeded(resultBooks)
        }
    }

    private fun createResultBooks(items: List<Item>): List<BookSearchResult> {
        val books = mutableListOf<BookSearchResult>()
        for (item in items) {
            val info = item.volumeInfo
            val id = item.id
            val title = info?.title ?: continue
            val authors = info.authors ?: listOf(context.getString(R.string.hyphen))
            val averageRating = info.averageRating
            val ratingsCount = info.ratingsCount ?: 0
            val description = info.description ?: ""
            val image = info.imageLinks?.thumbnail ?: ""
            books.add(
                BookSearchResult(
                    id, title, authors, averageRating, ratingsCount, description, image
                )
            )
        }
        return books
    }
}
