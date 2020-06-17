package com.example.bookmanager.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityMainBinding
import com.example.bookmanager.models.Book

class BookshelfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val books = createDummyData()
        setDataToRecyclerView(books)
        setFabClickListener()
        initToolbar()
    }

    private fun setDataToRecyclerView(data: MutableList<Book>) {
        val adapter = BookSearchAdapter(this, data).apply {
            update(data)
        }
        val manager = LinearLayoutManager(this)
        binding.myBookList.also {
            it.layoutManager = manager
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(this, manager.orientation))
        }
    }

    private fun createDummyData(): MutableList<Book> {
        val books = mutableListOf<Book>()
        for (i in 1..10) {
            val id = "abc"
            val image =
                "http://books.google.com/books/content?id=13gDwgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"
            val title = "鬼滅の刃(${i})"
            val authors = arrayListOf("吾峠呼世晴", "TEST")
            books.add(Book(id, title, authors, image))
        }
        return books
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
