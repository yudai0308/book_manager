package com.example.bookmanager.views

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookSearchBinding
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.models.BookSearchResultItem
import com.example.bookmanager.rooms.common.BookRepository
import com.example.bookmanager.rooms.entities.Author
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.ViewUtil
import com.example.bookmanager.viewmodels.BookResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 本検索ページのアクティビティ。
 */
class BookSearchActivity : AppCompatActivity() {

    private lateinit var view: View

    private val handler = Handler()

    // TODO: リポジトリは ViewModel 経由で操作したい
    private val repository by lazy { BookRepository(this) }

    private val viewModel by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(BookResultViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookSearchBinding>(
            this, R.layout.activity_book_search
        )
    }

    private lateinit var searchView: SearchView

    /**
     * 検索方法（フリーワード/タイトル/著者名）
     */
    private var searchMethod = ""

    /**
     * ユーザーが入力した検索文字列
     */
    private var searchQuery = ""

    /**
     * 追加検索のロード中であれば true。
     */
    private var nowLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            view = it.root
        }

        // `as Toolbar` がないとエラーになる。
        setSupportActionBar(binding.toolbar as Toolbar)

        supportActionBar?.apply {
            title = ""
            // ツールバーに戻るボタンを表示。
            setDisplayHomeAsUpEnabled(true)
        }

        initRecyclerView()
        initMessageView()
        initSpinner()
    }

    private fun initRecyclerView() {
        val adapter = BookSearchAdapter().apply {
            setOnItemClickListener(OnSearchResultClickListener())
        }
        binding.bookSearchResultList.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
            it.addOnScrollListener(AdditionalSearchListener())
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
        val items = listOf(
            getString(R.string.search_with_free_word),
            getString(R.string.search_with_title),
            getString(R.string.search_with_author)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.bookSearchSpinner.adapter = adapter
    }

    inner class OnSearchResultClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return
            val recyclerView: RecyclerView = binding.bookSearchResultList
            val position = recyclerView.getChildAdapterPosition(v)
            val resultItem = viewModel.bookSearchResult.value?.items?.get(position) ?: return
            val dialog = SimpleDialogFragment().apply {
                val activity = this@BookSearchActivity
                setTitle(resultItem.title)
                setMessage(activity.getString(R.string.dialog_add_book_msg))
                setPositiveButton(
                    activity.getString(R.string.yes), OnOkButtonClickListener(resultItem, v)
                )
                setNegativeButton(activity.getString(R.string.cancel), null)
            }
            dialog.show(supportFragmentManager, C.DIALOG_TAG_ADD_BOOK)
        }
    }

    inner class OnOkButtonClickListener(
        private val resultItem: BookSearchResultItem,
        private val view: View
    ) : DialogInterface.OnClickListener {

        override fun onClick(dialog: DialogInterface?, which: Int) {
            // 本棚に存在するか確認。
            if (repository.exists(resultItem.id)) {
                ViewUtil.showSnackBarLong(view, getString(R.string.exists_in_my_shelf))
                return
            }

            val newBook = Book.create(
                resultItem.id,
                resultItem.title,
                resultItem.description,
                resultItem.image,
                resultItem.infoLink,
                resultItem.publishedDate
            )

            val authors = Author.createAll(resultItem.authors)

            GlobalScope.launch {
                repository.insertBookWithAuthors(newBook, authors)
            }
            if (newBook.image.isNotBlank()) {
                saveImageToInternalStorage(newBook.image, newBook.id)
            }
            view.findViewById<ImageView>(R.id.book_search_bookmark).visibility = View.VISIBLE
            ViewUtil.showSnackBarLong(view, C.ADD_BOOK)
        }
    }

    private fun saveImageToInternalStorage(url: String, fileName: String) {
        val activity = this
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = Glide.with(activity).asBitmap().load(url).submit().get()
            FileIO.saveBookImage(activity, bitmap, fileName)
        }
    }

    private fun hideCenterProgressBar() {
        binding.bookSearchProgressBarCenter.apply {
            visibility = View.INVISIBLE
        }
    }

    private fun showCenterProgressBar() {
        binding.bookSearchProgressBarCenter.apply {
            visibility = View.VISIBLE
            bringToFront()
        }
    }

    private fun hideBottomProgressBar() {
        val progressBar = binding.root.findViewById<ProgressBar>(R.id.book_search_progress_bar_bottom)
        progressBar?.apply {
            visibility = View.INVISIBLE
        }
    }

    private fun showBottomProgressBar() {
        val progressBar = binding.root.findViewById<ProgressBar>(R.id.book_search_progress_bar_bottom)
        progressBar?.apply {
            visibility = View.VISIBLE
            bringToFront()
        }
    }

    private fun showCenterMessage(msg: String) {
        handler.post {
            binding.bookSearchMessage.apply {
                this.text = msg
                visibility = View.VISIBLE
            }
        }
    }

    private fun hideCenterMessage() {
        binding.bookSearchMessage.apply {
            visibility = View.INVISIBLE
        }
    }

    private fun clearFocus() {
        handler.post {
            searchView.clearFocus()
        }
    }

    private fun backToTop() {
        handler.post {
            binding.bookSearchResultList.scrollToPosition(0)
        }
    }

    inner class NewSearchCallback : BookResultViewModel.SearchCallback {
        override fun onSearchStart() {
            showCenterProgressBar()
        }

        override fun onSearchSucceeded(result: BookSearchResult) {
            hideCenterProgressBar()
            clearFocus()
            backToTop()
            if (result.itemCount == 0) {
                showCenterMessage(getString(R.string.item_not_fount))
            } else {
                hideCenterMessage()
            }
        }

        override fun onSearchFailed() {
            hideCenterMessage()
            showCenterMessage(getString(R.string.search_error))
            hideCenterProgressBar()
        }
    }

    inner class AdditionalSearchCallback : BookResultViewModel.SearchCallback {
        override fun onSearchStart() {
            showBottomProgressBar()
        }

        override fun onSearchSucceeded(result: BookSearchResult) {
            hideBottomProgressBar()
            nowLoading = false
        }

        override fun onSearchFailed() {
            hideBottomProgressBar()
            nowLoading = false
        }
    }

    inner class AdditionalSearchListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val totalCount = recyclerView.adapter?.itemCount ?: 0
            val childCount = recyclerView.childCount
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val firstPosition = manager.findFirstVisibleItemPosition()

            // 何度もリクエストしないようにロード中は何もしない。
            if (nowLoading) {
                return
            }

            if (totalCount == childCount + firstPosition) {
                nowLoading = true
                viewModel.searchBooks(
                    searchQuery,
                    searchMethod,
                    BookResultViewModel.SearchType.ADDITIONAL,
                    AdditionalSearchCallback()
                )
            }
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
                    searchQuery = query
                    searchMethod = binding.bookSearchSpinner.selectedItem.toString()
                    viewModel.searchBooks(
                        searchQuery,
                        searchMethod,
                        BookResultViewModel.SearchType.NEW,
                        NewSearchCallback()
                    )
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
