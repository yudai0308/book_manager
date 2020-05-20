package com.example.bookmanager.views

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookmanager.R
import com.example.bookmanager.models.Item
import com.example.bookmanager.models.ResultBook
import com.example.bookmanager.models.SearchResult
import com.example.bookmanager.rooms.dao.BookDao
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.Const
import com.example.bookmanager.utils.Libs
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class BookSearchActivity : AppCompatActivity() {

    private lateinit var view: ConstraintLayout
    private val handler = Handler()
    private var resultBooks: MutableList<ResultBook> = mutableListOf()
    private lateinit var bookDao: BookDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        view = findViewById(R.id.book_search_root)
        val db = Room.databaseBuilder(
            this,
            BookDatabase::class.java,
            Const.DB_NAME
        ).build()
        bookDao = db.bookDao()

        initMessageView()
        initSearchBar()
        initSpinner()
    }

    private fun initMessageView() {
        findViewById<TextView>(R.id.book_search_message).apply {
            text = "検索結果がありません。"
            visibility = View.VISIBLE
        }
    }

    private fun initSearchBar() {
        val searchBar: MaterialSearchBar = findViewById(R.id.book_search_bar)
        searchBar.setOnSearchActionListener(SearchActionListener())
        searchBar.openSearch()
    }

    private fun initSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner_book_search_type)
        val items = listOf(Const.SEARCH_FREE_WORD, Const.SEARCH_TITLE, Const.SEARCH_AUTHOR)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private inner class SearchActionListener : MaterialSearchBar.OnSearchActionListener {
        override fun onButtonClicked(buttonCode: Int) {}

        override fun onSearchStateChanged(enabled: Boolean) {}

        override fun onSearchConfirmed(text: CharSequence?) {
            showProgressBar()
            // 入力値を取得してパラメーター付き URL を作成。
            val searchBar: MaterialSearchBar = findViewById(R.id.book_search_bar)
            val spinner: Spinner = findViewById(R.id.spinner_book_search_type)
            val keyword = searchBar.text
            val searchType = spinner.selectedItem.toString()
            val param = createUrlWithParameter(searchType, keyword)
            val url = Const.BOOK_SEARCH_API_URL + param

            // API へリクエストを送る。
            val req = Request.Builder().url(url).get().build()
            val client = OkHttpClient.Builder().apply {
                readTimeout(10, TimeUnit.SECONDS)
                connectTimeout(10, TimeUnit.SECONDS)
            }.build()
            val call = client.newCall(req)
            call.enqueue(BookSearchCallback())
        }
    }

    private fun removeAllItems() {
        val recyclerView: RecyclerView = findViewById(R.id.book_search_results)
        val adapter = if (recyclerView.adapter != null) {
            recyclerView.adapter as BookListAdapter
        } else {
            return
        }
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
        override fun onFailure(call: Call, e: IOException) {
            handler.post {
                hideProgressBar()
                showMessage(Const.CONNECTION_FAILURE_MSG)
                removeAllItems()
            }
        }

        override fun onResponse(call: Call, response: Response) {
            handler.post {
                hideProgressBar()
                hideMessage()
                removeAllItems()
            }

            val resBody = response.body?.string()
            val adapter = Moshi.Builder().build().adapter(SearchResult::class.java)
            if (resBody.isNullOrBlank()) {
                Libs.showSnackBar(view, getString(R.string.search_error))
                return
            }

            val result = adapter.fromJson(resBody)
            if (result == null) {
                Libs.showSnackBar(view, getString(R.string.search_error))
                return
            }

            handler.post { createSearchResultView(result) }
        }
    }

    private fun createSearchResultView(result: SearchResult) {
        val items = result.items
        if (items.isNullOrEmpty()) {
            showMessage(Const.CONNECTION_FAILURE_MSG)
            return
        }

        createBooksFromItems(items)
        if (resultBooks.isNullOrEmpty()) {
            showMessage(getString(R.string.item_not_fount))
            return
        }

        // FIXME: 初回はアダプターを生成、２回目以降はアダプターを再利用
        val manager = LinearLayoutManager(this)
        val adapter = BookListAdapter(
            this,
            resultBooks,
            OnSearchResultClickListener()
        )
        findViewById<RecyclerView>(R.id.book_search_results).apply {
            layoutManager = manager
            this.adapter = adapter
        }
    }

    private fun createBooksFromItems(items: List<Item>) {
        val books = mutableListOf<ResultBook>()
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

            books.add(ResultBook(id, title, authors, image))
        }
        resultBooks = books
    }

    inner class OnSearchResultClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return
            val recyclerView: RecyclerView = findViewById(R.id.book_search_results)
            val position = recyclerView.getChildAdapterPosition(v)
            val resultBook = resultBooks[position]
            AlertDialog.Builder(this@BookSearchActivity)
                .setTitle(getString(R.string.dialog_add_book_title))
                .setMessage(getString(R.string.dialog_add_book_msg))
                .setPositiveButton(
                    getString(R.string.button_yes),
                    OnOkButtonClickListener(resultBook)
                )
                .setNegativeButton(getString(R.string.button_no), null)
                .show()
        }
    }

    inner class OnOkButtonClickListener(private val resultBook: ResultBook) :
        DialogInterface.OnClickListener {

        override fun onClick(dialog: DialogInterface?, which: Int) {
            // 本棚に存在するか確認。
            if (haveBook(resultBook.id)) {
                Libs.showSnackBar(view, getString(R.string.exists_in_my_shelf))
                return
            }
            val now = System.currentTimeMillis()
            val newBook = Book(
                resultBook.id,
                resultBook.title,
                resultBook.image,
                null,
                now,
                now
            )
            runBlocking {
                bookDao.insert(newBook)
            }
            Libs.showSnackBar(view, Const.ADD_BOOK)
        }
    }

    private fun haveBook(id: String): Boolean {
        val count = runBlocking {
            bookDao.count(id)
        }
        return count > 0
    }

    private fun hideProgressBar() {
        findViewById<ProgressBar>(R.id.book_search_progress_bar).apply {
            visibility = View.INVISIBLE
        }
    }

    private fun showProgressBar() {
        findViewById<ProgressBar>(R.id.book_search_progress_bar).apply {
            visibility = View.VISIBLE
            bringToFront()
        }
    }

    private fun showMessage(msg: String) {
        findViewById<TextView>(R.id.book_search_message).apply {
            this.text = msg
            visibility = View.VISIBLE
        }
    }

    private fun hideMessage() {
        findViewById<TextView>(R.id.book_search_message).apply {
            visibility = View.INVISIBLE
        }
    }

}
