package com.example.bookmanager.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityMainBinding
import com.example.bookmanager.viewmodels.BookshelfViewModel

class BookshelfActivity : AppCompatActivity() {

    private lateinit var viewModel: BookshelfViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(BookshelfViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        initToolbar()
        initRecyclerView()
        setFabClickListener()
    }

    private fun initRecyclerView() {
        val adapter = BookshelfAdapter()
        val manager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.bookshelfRoot.also {
            it.layoutManager = manager
            it.adapter = adapter
//            it.addItemDecoration(DividerItemDecoration(this, manager.orientation))
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
