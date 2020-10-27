package com.example.bookmanager.views

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookSearchBinding
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.rooms.common.DaoController
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.Libs
import com.example.bookmanager.viewmodels.BookResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 本検索ページのアクティビティ。
 */
class BookSearchActivity : AppCompatActivity() {

    private lateinit var view: View

    private val handler = Handler()

    private val dao by lazy { DaoController(this) }

    private val db by lazy {
        Room.databaseBuilder(this, BookDatabase::class.java, C.DB_NAME).build()
    }

    private val bookDao by lazy { db.bookDao() }

    private val viewModel by lazy {
        ViewModelProvider(this).get(BookResultViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookSearchBinding>(
            this, R.layout.activity_book_search
        )
    }

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        view = binding.root

        // as Toolbar がないとエラーになる。
        setSupportActionBar(binding.toolbar as Toolbar)

        supportActionBar?.apply {
            title = ""
            // ツールバーに戻るボタンを表示。
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        super.onResume()

        // TODO: onCreate() に移動するべき？
        initMessageView()
        initSpinner()

        val adapter = BookSearchAdapter().apply {
            setListener(OnSearchResultClickListener())
        }
        binding.bookSearchResultList.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        }
    }

    private fun initMessageView() {
        binding.bookSearchMessage.apply {
            text = getString(R.string.item_not_fount)
            visibility = View.VISIBLE
        }
    }

    private fun initSpinner() {
        val spinner: Spinner = binding.bookSearchSpinner
        val items = listOf(C.SEARCH_FREE_WORD, C.SEARCH_TITLE, C.SEARCH_AUTHOR)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    inner class OnSearchResultClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return
            val recyclerView: RecyclerView = binding.bookSearchResultList
            val position = recyclerView.getChildAdapterPosition(v)
            // FIXME: resultBook が null だった場合の処理を検討。
            val resultBook = viewModel.resultBooks.value?.get(position)
            resultBook ?: return
            val dialog = SimpleDialogFragment().apply {
                val activity = this@BookSearchActivity
                setTitle(activity.getString(R.string.dialog_add_book_title))
                setMessage(activity.getString(R.string.dialog_add_book_msg))
                setPositiveButton(
                    activity.getString(R.string.yes), OnOkButtonClickListener(resultBook)
                )
                setNegativeButton(activity.getString(R.string.cancel), null)
            }
            dialog.show(supportFragmentManager, C.DIALOG_TAG_ADD_BOOK)
        }
    }

    inner class OnOkButtonClickListener(private val resultBook: BookSearchResult) :
        DialogInterface.OnClickListener {

        override fun onClick(dialog: DialogInterface?, which: Int) {
            // 本棚に存在するか確認。
            if (haveBook(resultBook.id)) {
                Libs.showSnackBar(view, getString(R.string.exists_in_my_shelf))
                return
            }

            val newBook = Book.create(
                resultBook.id, resultBook.title, resultBook.description, resultBook.image
            )

            val authors = Author.createAll(resultBook.authors)

            GlobalScope.launch {
                dao.insertBookWithAuthors(newBook, authors)
            }
            if (!newBook.image.isBlank()) {
                saveImageToInternalStorage(newBook.image, newBook.id)
            }
            Libs.showSnackBar(view, C.ADD_BOOK)
        }
    }

    // TODO: 画像保存の処理はアクティビティから分離させたい。
    private fun saveImageToInternalStorage(url: String, fileName: String) {
        val activity = this
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = Glide.with(activity).asBitmap().load(url).submit().get()
            FileIO.saveBookImage(activity, bitmap, fileName)
        }
    }

    private fun haveBook(id: String): Boolean {
        val flag = runBlocking {
            bookDao.exists(id)
        }
        return flag > 0
    }

    private fun hideProgressBar() {
        binding.bookSearchProgressBar.apply {
            visibility = View.INVISIBLE
        }
    }

    private fun showProgressBar() {
        binding.bookSearchProgressBar.apply {
            visibility = View.VISIBLE
            bringToFront()
        }
    }

    private fun showMessage(msg: String) {
        handler.post {
            binding.bookSearchMessage.apply {
                this.text = msg
                visibility = View.VISIBLE
            }
        }
    }

    private fun hideMessage() {
        binding.bookSearchMessage.apply {
            visibility = View.INVISIBLE
        }
    }

    private fun clearFocus() {
        handler.post {
            searchView.clearFocus()
        }
    }

    inner class SearchCallback : BookResultViewModel.SearchCallback {
        override fun onSearchStart() {
            showProgressBar()
        }

        override fun onSearchSucceeded(resultBooks: List<BookSearchResult>) {
            hideProgressBar()
            clearFocus()
            if (resultBooks.isEmpty()) {
                showMessage(getString(R.string.item_not_fount))
            } else {
                hideMessage()
            }
        }

        override fun onSearchFailed() {
            hideMessage()
            showMessage(getString(R.string.search_error))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_search_menu, menu)

        searchView = menu?.findItem(R.id.toolbar_search_book)?.actionView as SearchView
        searchView.apply {
            isIconified = false
            queryHint = getString(R.string.query_hint_book_search)
            // SearchView の挙動を定義
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                // 検索ボタンが押されたときの処理
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query == null || query == "") {
                        return true
                    }
                    val searchType = binding.bookSearchSpinner.selectedItem.toString()
                    viewModel.searchBook(query, searchType, SearchCallback())
                    return true
                }

                // テキストが変更されたときの処理（なにもしない）
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
