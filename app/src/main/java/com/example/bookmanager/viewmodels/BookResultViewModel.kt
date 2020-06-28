package com.example.bookmanager.viewmodels

import android.widget.Spinner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookmanager.databinding.ActivityBookSearchBinding
import com.example.bookmanager.models.Item
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.models.SearchResult
import com.example.bookmanager.utils.Const
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException

class BookResultViewModel : ViewModel() {

    private val _resultBooks: MutableLiveData<List<BookSearchResult>> = MutableLiveData()
    val resultBooks: LiveData<List<BookSearchResult>> = _resultBooks

    private var searchCallback: SearchCallback? = null

    interface SearchCallback {
        fun onSearchStart()
        fun onSearchSucceeded(resultBooks: List<BookSearchResult>)
        fun onSearchFailed()
    }

    fun searchBook(binding: ActivityBookSearchBinding, callback: SearchCallback) {
        searchCallback = callback
        searchCallback?.onSearchStart()
        val url = createUrl(binding)
        fetch(url)
    }

    private fun createUrl(binding: ActivityBookSearchBinding): String {
        val searchBar: MaterialSearchBar = binding.bookSearchBar
        val spinner: Spinner = binding.spinnerBookSearchType
        val keyword = searchBar.text
        val searchType = spinner.selectedItem.toString()
        val param = createUrlWithParameter(searchType, keyword)
        return Const.BOOK_SEARCH_API_URL + param
    }


    private fun createUrlWithParameter(
        type: String,
        keyword: String,
        max: Int = Const.MAX_RESULTS_COUNT,
        index: Int = 0
    ): String {
        var param = Const.ADD_QUERY
        param += when (type) {
            Const.SEARCH_TITLE -> Const.PARAM_TITLE
            Const.SEARCH_AUTHOR -> Const.PARAM_AUTHOR
            else -> ""
        }
        return param + keyword + Const.PARAM_MAX + max + Const.PARAM_INDEX + index
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

            val title = if (info?.title != null) {
                info.title as String
            } else {
                continue
            }

            val authors = if (info.authors != null) {
                info.authors as List<String>
            } else {
                listOf(Const.UNKNOWN)
            }

            val image = if (info.imageLinks?.thumbnail != null) {
                info.imageLinks?.thumbnail as String
            } else {
                ""
            }

            books.add(BookSearchResult(id, title, authors, image))
        }
        return books
    }
}
