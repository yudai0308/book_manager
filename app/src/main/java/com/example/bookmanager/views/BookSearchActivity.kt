package com.example.bookmanager.views

import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookSearchBinding
import com.example.bookmanager.rooms.dao.BookDao
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.Const
import com.example.bookmanager.utils.Libs
import com.example.bookmanager.viewmodels.BookResultViewModel
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

        val adapter = BookSearchAdapter().apply {
            setListener(OnSearchResultClickListener())
        }
        binding.bookSearchResults.also {
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
            viewModel.searchBook(binding, object : BookResultViewModel.SearchCallback {
                override fun onSearchStart() {
                    showProgressBar()
                }

                override fun onSearchSucceeded(resultBooks: List<com.example.bookmanager.models.Book>) {
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
            val dialog = SimpleDialogFragment().apply {
                val activity = this@BookSearchActivity
                setTitle(activity.getString(R.string.dialog_add_book_title))
                setMessage(activity.getString(R.string.dialog_add_book_msg))
                setPositiveButton(
                    activity.getString(R.string.button_yes),
                    OnOkButtonClickListener(resultBook)
                )
                setNegativeButton(activity.getString(R.string.button_no), null)
            }
            dialog.show(supportFragmentManager, Const.ADD_BOOK_DIALOG_TAG)
        }
    }

    inner class OnOkButtonClickListener(private val resultBook: com.example.bookmanager.models.Book) :
        DialogInterface.OnClickListener {

        override fun onClick(dialog: DialogInterface?, which: Int) {
            // 本棚に存在するか確認。
            if (haveBook(resultBook.id)) {
                Libs.showSnackBar(view, getString(R.string.exists_in_my_shelf))
                return
            }
            val now = System.currentTimeMillis()
            val newBook = Book(
                resultBook.id, resultBook.title, resultBook.image,
                null, now, now
            )
            GlobalScope.launch {
                bookDao.insert(newBook)
            }
            if (!newBook.image.isNullOrBlank()) {
                saveImageToInternalStorage(newBook.image, newBook.id)
            }
            Libs.showSnackBar(view, Const.ADD_BOOK)
        }
    }

    // TODO: 画像保存の処理はアクティビティから分離させたい。
    private fun saveImageToInternalStorage(url: String, fileName: String) =
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = Glide.with(this@BookSearchActivity)
                .asBitmap()
                .load(url)
                .submit()
                .get()
            saveToInternalStorage(bitmap, fileName)
        }

    private fun saveToInternalStorage(bitmap: Bitmap, fileName: String): Boolean {
        return try {
            val contextWrapper = ContextWrapper(this)
            val directory =
                contextWrapper.getDir(Const.DIRECTORY_NAME_BOOK_IMAGE, Context.MODE_PRIVATE)
            val path = File(directory, fileName)
            FileOutputStream(path).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            true
        } catch (e: IOException) {
            Log.e(null, "画像の保存に失敗。")
            false
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
