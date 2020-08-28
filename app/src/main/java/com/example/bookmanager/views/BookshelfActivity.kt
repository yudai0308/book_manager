package com.example.bookmanager.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookshelfBinding
import com.example.bookmanager.utils.C
import com.example.bookmanager.viewmodels.BookshelfViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 本棚ページのアクティビティ。
 */
class BookshelfActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(BookshelfViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookshelfBinding>(this, R.layout.activity_bookshelf)
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
        val spanCount = resources.getInteger(R.integer.bookshelf_grid_span_count)
        val spacing = resources.getInteger(R.integer.bookshelf_grid_spacing)

        val listener = View.OnClickListener {
            val position = binding.bookshelfRoot.getChildAdapterPosition(it)
            val book = viewModel.books.value?.get(position)
            // TODO: book が null だった場合の処理。
            book ?: return@OnClickListener
            startActivity(Intent(applicationContext, BookDetailActivity::class.java).apply {
                putExtra(C.BOOK_ID, book.id)
            })
        }

        val adapter = BookshelfAdapter().apply {
            setListener(listener)
        }

        val manager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)

        binding.bookshelfRoot.also {
            it.layoutManager = manager
            it.adapter = adapter
            it.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, true))
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
}
