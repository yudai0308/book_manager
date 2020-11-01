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

    interface SearchCallback {
        fun onSearchStart()
        fun onSearchSucceeded(resultBooks: List<BookSearchResult>)
        fun onSearchFailed()
    }

    fun searchBook(query: String, searchType: String, callback: SearchCallback) {
        searchCallback = callback
        searchCallback?.onSearchStart()
        val param = createUrlWithParameter(searchType, query)
        val url = C.BOOK_SEARCH_API_URL + param
        fetch(url)
    }

    private fun createUrlWithParameter(
        type: String, keyword: String, max: Int = C.MAX_RESULTS_COUNT, index: Int = 0
    ): String {
        var param = C.ADD_QUERY
        param += when (type) {
            C.SEARCH_TITLE -> C.PARAM_TITLE
            C.SEARCH_AUTHOR -> C.PARAM_AUTHOR
            else -> ""
        }
        return param + keyword + C.PARAM_MAX + max + C.PARAM_INDEX + index
    }

    private fun fetch(url: String) {
        val req = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder().build()
        val call = client.newCall(req)
        call.enqueue(BookSearchCallback())
    }

    inner class BookSearchCallback : Callback {
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
            val resultBooks = if (result.items == null) {
                listOf()
            } else {
                createResultBooks(result.items as List<Item>)
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
