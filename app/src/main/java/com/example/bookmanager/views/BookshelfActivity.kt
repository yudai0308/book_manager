package com.example.bookmanager.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

    private val daoController by lazy { BookRepository(this) }

    private lateinit var adapter: BookshelfAdapter

    private var selectedView: View? = null

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

        initToolbar()
        initRecyclerView()
        setFabClickListener()
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch { viewModel.reload() }
    }

    private fun initRecyclerView() {
        val listener = View.OnClickListener {
            val position = binding.bookshelfBookList.getChildAdapterPosition(it)
            startBookDetailActivity(position)
        }

        adapter = BookshelfAdapter().apply {
            setListener(listener)
            setCallback(object : BookshelfAdapter.Callback {
                override fun onBindViewHolder(view: View) {
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

    private fun initToolbar() {
        val toolbar: Toolbar = binding.toolbar.apply {
            setTitle(R.string.toolbar_title_bookshelf)
        } as Toolbar
        setSupportActionBar(toolbar)
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
            it.add(Menu.NONE, 0, Menu.NONE, "詳細")
            it.add(Menu.NONE, 1, Menu.NONE, "削除")
        }

        view?.let {
            selectedView = view
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = selectedView?.let {
            binding.bookshelfBookList.getChildAdapterPosition(it)
        }
        position ?: return super.onContextItemSelected(item)

        when (item.itemId) {
            MENU_DETAIL -> {
                selectedView?.let { startBookDetailActivity(position) }
            }
            MENU_DELETE -> {
                val book =
                    viewModel.books.value?.get(position) ?: return super.onContextItemSelected(item)
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
                    daoController.deleteBook(book)
                    FileIO.deleteBookImage(this@BookshelfActivity, book.id)
                    viewModel.reload()
                }
            })
            it.setNegativeButton(getString(R.string.cancel), null)
        }
    }
}
