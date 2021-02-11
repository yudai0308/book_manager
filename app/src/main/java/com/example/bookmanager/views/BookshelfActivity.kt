package com.example.bookmanager.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookshelfBinding
import com.example.bookmanager.rooms.common.BookRepository
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.ViewUtil
import com.example.bookmanager.viewmodels.BookshelfViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 本棚ページのアクティビティ。
 */
class BookshelfActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(BookshelfViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookshelfBinding>(this, R.layout.activity_bookshelf)
    }

    private val bookRepository by lazy { BookRepository(this) }

    private var selectedBook: View? = null

    private lateinit var selectedButton: Button

    companion object {
        const val MENU_DETAIL = 0
        const val MENU_DELETE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        selectedButton = binding.filterButtons.filterButtonAll
        initToolbar()
        initRecyclerView()
        setFabClickListener()
        setFilterButtonsClickListener()
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch { viewModel.fetchAllBooks() }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.toolbar.apply {
            setTitle(R.string.toolbar_title_bookshelf)
        } as Toolbar
        setSupportActionBar(toolbar)
    }

    private fun initRecyclerView() {
        val listener = View.OnClickListener {
            val position = binding.bookshelfBookList.getChildAdapterPosition(it)
            startBookDetailActivity(position)
        }

        val adapter = BookshelfAdapter().apply {
            setOnClickListener(listener)
            setOnBindViewHolderListener(object : BookshelfAdapter.OnBindViewHolderListener {
                override fun onBound(view: View) {
                    registerForContextMenu(view)
                }
            })
        }

        val spanCount = resources.getInteger(R.integer.bookshelf_grid_span_count)
        val manager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)

        binding.bookshelfBookList.also {
            it.layoutManager = manager
            it.adapter = adapter
            it.setHasFixedSize(true)
            it.addItemDecoration(GridSpacingItemDecoration(
                this,
                ViewUtil.dpToPx(this, 100F).toInt()
            ))
        }
    }

    private fun setFabClickListener() {
        binding.fabAddBook.setOnClickListener {
            val intent = Intent(applicationContext, BookSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setFilterButtonsClickListener() {
        val buttons = binding.filterButtons.run {
            listOf(
                filterButtonAll,
                filterButtonWantToRead,
                filterButtonReading,
                filterButtonFinished
            )
        }
        buttons.forEach { button ->
            button.setOnClickListener {
                showBooksAccordingToSelectedButton(it as Button)
            }
        }
    }

    private fun showBooksAccordingToSelectedButton(clickedButton: Button) {
        if (clickedButton.id == selectedButton.id) {
            return
        }

        val selectedBackground = ContextCompat.getDrawable(this, R.drawable.btn_filter_selected)
        val selectableBackground = ContextCompat.getDrawable(this, R.drawable.btn_filter_selectable)
        selectedButton.background = selectableBackground
        selectedButton = clickedButton
        clickedButton.background = selectedBackground

        when (clickedButton.id) {
            R.id.filter_button_all -> GlobalScope.launch { viewModel.fetchAllBooks() }
            R.id.filter_button_want_to_read -> GlobalScope.launch { viewModel.fetchBooksWantToRead() }
            R.id.filter_button_reading -> GlobalScope.launch { viewModel.fetchBooksReading() }
            R.id.filter_button_finished -> GlobalScope.launch { viewModel.fetchBooksFinished() }
        }
    }

    private fun startBookDetailActivity(position: Int) {
        val book = viewModel.books.value?.get(position)
        // TODO: book が null だった場合の処理。
        book ?: return
        startActivity(Intent(applicationContext, BookDetailActivity::class.java).apply {
            putExtra(C.BOOK_ID, book.id)
        })
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu?.let {
            it.add(Menu.NONE, 0, Menu.NONE, getString(R.string.detail))
            it.add(Menu.NONE, 1, Menu.NONE, getString(R.string.delete))
        }

        view?.let {
            selectedBook = view
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = selectedBook?.let {
            binding.bookshelfBookList.getChildAdapterPosition(it)
        }
        position ?: return super.onContextItemSelected(item)

        when (item.itemId) {
            MENU_DETAIL -> {
                selectedBook?.let { startBookDetailActivity(position) }
            }
            MENU_DELETE -> {
                val book = viewModel.books.value?.get(position) ?: return super.onContextItemSelected(item)
                val dialog = createDeleteConfirmationDialog(book)
                dialog.show(supportFragmentManager, C.DIALOG_TAG_DELETE_BOOK)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun createDeleteConfirmationDialog(book: Book): SimpleDialogFragment {
        return SimpleDialogFragment().also {
            it.setTitle(book.title)
            it.setMessage(getString(R.string.delete_dialog_message))
            it.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                runBlocking {
                    bookRepository.deleteBook(book)
                    FileIO.deleteBookImage(this@BookshelfActivity, book.id)
                    viewModel.fetchAllBooks()
                }
            })
            it.setNegativeButton(getString(R.string.cancel), null)
        }
    }
}
