package com.example.bookmanager.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityMainBinding
import com.example.bookmanager.viewmodels.BookshelfViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BookshelfActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(BookshelfViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
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

        val intent = Intent(applicationContext, BookReviewActivity::class.java)
        val listener = View.OnClickListener { startActivity(intent) }

        val adapter = BookshelfAdapter().apply {
            setListener(listener)
        }
        val manager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
        binding.bookshelfRoot.also {
            it.layoutManager = manager
            it.adapter = adapter
            it.addItemDecoration(
                GridSpacingItemDecoration(spanCount, spacing, true)
            )
        }
    }

    private fun setFabClickListener() {
        binding.fabAddBook.setOnClickListener {
            val intent = Intent(applicationContext, BookSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            setTitle(R.string.toolbar_title)
            setTitleTextColor(Color.WHITE)
        }
        setSupportActionBar(toolbar)
    }
}
