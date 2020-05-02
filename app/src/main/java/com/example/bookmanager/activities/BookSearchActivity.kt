package com.example.bookmanager.activities

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.models.Book
import com.example.bookmanager.R
import com.example.bookmanager.utils.Const
import com.example.bookmanager.models.Item
import com.example.bookmanager.models.SearchResult
import com.example.bookmanager.views.BookListAdapter
import com.google.android.material.snackbar.Snackbar
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.IOException

class BookSearchActivity : AppCompatActivity() {

    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        initSearchBar()
        initSpinner()
        createDummyResults()
    }

    private fun initSearchBar() {
        val searchBar = findViewById<MaterialSearchBar>(R.id.book_search_bar)
        searchBar.setOnSearchActionListener(SearchActionListener())
        searchBar.openSearch()
    }

    private fun initSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner_book_search_type)
        val items = listOf(Const.SEARCH_FREE_WORD, Const.SEARCH_TITLE, Const.SEARCH_AUTHOR)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun createDummyResults() {
        val recyclerView = findViewById<RecyclerView>(R.id.book_search_results)
        val adapter = BookListAdapter(
            this,
            createDummyData(),
            OnSearchResultClickListener()
        )
        val manager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = manager

        val decorator = DividerItemDecoration(this, manager.orientation)
        recyclerView.addItemDecoration(decorator)
    }

    private fun createDummyData(): MutableList<Book> {
        val books = mutableListOf<Book>()
        for (i in 1..10) {
            books.add(
                Book(
                    "abc",
                    "進撃の巨人${i}",
                    listOf("諫山創"),
                    "http://books.google.com/books/content?id=b_e3DwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
                )
            )
        }
        return books
    }

    private inner class SearchActionListener : MaterialSearchBar.OnSearchActionListener {
        override fun onButtonClicked(buttonCode: Int) {}

        override fun onSearchStateChanged(enabled: Boolean) {}

        override fun onSearchConfirmed(text: CharSequence?) {
            // 現在表示されているリストをクリア。
            removeAllItem()
            // 入力値を取得してパラメーター付き URL を作成。
            val searchBar = findViewById<MaterialSearchBar>(R.id.book_search_bar)
            val spinner = findViewById<Spinner>(R.id.spinner_book_search_type)
            val keyword = searchBar.text
            val searchType = spinner.selectedItem.toString()
            val param = createUrlWithParameter(searchType, keyword)
            val url = Const.BOOK_SEARCH_API_URL + param

            val req = Request.Builder().url(url).get().build()
            val call = OkHttpClient().newCall(req)
            call.enqueue(BookSearchCallback())
        }
    }

    private fun removeAllItem() {
        val recyclerView = findViewById<RecyclerView>(R.id.book_search_results)
        val adapter = recyclerView.adapter as BookListAdapter
        val cnt = adapter.itemCount
        if (cnt < 1) return
        adapter.removeAll()
    }

    private fun createUrlWithParameter(
        type: String,
        keyword: String,
        max: Int = 30,
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

    inner class BookSearchCallback : Callback {
        override fun onFailure(call: Call, e: IOException) {}

        override fun onResponse(call: Call, response: Response) {
            val resBody = response.body?.string()
            val adapter = Moshi.Builder().build().adapter(SearchResult::class.java)
            if (resBody.isNullOrBlank()) {
                showSnackBar(getString(R.string.search_error))
                return
            }

            val result = adapter.fromJson(resBody)
            if (result == null) {
                showSnackBar(getString(R.string.search_error))
                return
            }

            handler.post { createSearchResultView(result) }
        }
    }

    private fun createSearchResultView(result: SearchResult) {
        val items = result.items
        if (items.isNullOrEmpty()) {
            showSnackBar(getString(R.string.search_no_item))
            return
        }

        val books = createBooksFromItems(items)
        if (books.isEmpty()) {
            showSnackBar(getString(R.string.search_no_item))
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.book_search_results)
        val adapter = BookListAdapter(
            this,
            books as MutableList<Book>
        )
        recyclerView.adapter = adapter
    }

    private fun createBooksFromItems(items: List<Item>): List<Book> {
        val books = mutableListOf<Book>()
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

            books.add(Book(id, title, authors, image))
        }
        return books
    }

    inner class OnSearchResultClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            AlertDialog.Builder(this@BookSearchActivity)
                .setTitle(getString(R.string.dialog_add_book_title))
                .setMessage(getString(R.string.dialog_add_book_msg))
                .setPositiveButton(getString(R.string.button_yes), OnOkButtonClickListener())
                .setNegativeButton(getString(R.string.button_no), null)
                .show()
        }
    }

    inner class OnOkButtonClickListener(): DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            showSnackBar("追加したよ！")
        }

    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(
            findViewById(R.id.book_search_root),
            msg,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
