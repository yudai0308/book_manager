package com.example.bookmanager.views

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookSearchBinding
import com.example.bookmanager.models.ResultBook
import com.example.bookmanager.rooms.dao.BookDao
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.Const
import com.example.bookmanager.utils.Libs
import com.example.bookmanager.viewmodels.BookResultViewModel
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.coroutines.runBlocking

class BookSearchActivity : AppCompatActivity() {

    private lateinit var view: View
    private val handler = Handler()

    private lateinit var bookDao: BookDao
    private lateinit var viewModel: BookResultViewModel
    private lateinit var binding: ActivityBookSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        viewModel = ViewModelProvider(this).get(BookResultViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_search)

        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        view = binding.root
        // TODO: DB 関連の処理はモデルに持たせる。
        bookDao = Room.databaseBuilder(
            this,
            BookDatabase::class.java,
            Const.DB_NAME
        ).build().bookDao()
    }

    override fun onResume() {
        super.onResume()

        initMessageView()
        initSearchBar()
        initSpinner()

        binding.bookSearchResults.also {
            it.adapter = BookListAdapter(this, listOf(), OnSearchResultClickListener())
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

    private fun initSearchBar() {
        binding.bookSearchBar.apply {
            setOnSearchActionListener(SearchActionListener())
            openSearch()
        }
    }

    private fun initSpinner() {
        val spinner: Spinner = binding.spinnerBookSearchType
        val items = listOf(Const.SEARCH_FREE_WORD, Const.SEARCH_TITLE, Const.SEARCH_AUTHOR)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private inner class SearchActionListener : MaterialSearchBar.OnSearchActionListener {
        override fun onButtonClicked(buttonCode: Int) {}

        override fun onSearchStateChanged(enabled: Boolean) {}

        override fun onSearchConfirmed(text: CharSequence?) {
            viewModel.onSearch(binding, object : BookResultViewModel.SearchCallback {
                override fun onStartSearch() {
                    showProgressBar()
                }

                override fun onSucceededSearch(resultBooks: List<ResultBook>) {
                    hideProgressBar()
                    clearFocus()
                    if (resultBooks.isEmpty()) {
                        showMessage(getString(R.string.item_not_fount))
                    } else {
                        hideMessage()
                    }
                }

                override fun onFailedStart() {
                    hideMessage()
                    showMessage(getString(R.string.search_error))
                }
            })
        }
    }

    inner class OnSearchResultClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return
            val recyclerView: RecyclerView = binding.bookSearchResults
            val position = recyclerView.getChildAdapterPosition(v)
            // FIXME: resultBook が null だった場合の処理を検討。
            val resultBook = viewModel.resultBooks.value?.get(position)
            resultBook ?: return
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
            binding.bookSearchBar.clearFocus()
        }
    }

}
