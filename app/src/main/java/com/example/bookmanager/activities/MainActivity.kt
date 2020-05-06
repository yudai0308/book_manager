package com.example.bookmanager.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.R
import com.example.bookmanager.models.ResultBook
import com.example.bookmanager.views.BookListAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val books = createDummyData()
        setDataToRecyclerView(books)
        setFabClickListener()
        initToolbar()
    }

    private fun setDataToRecyclerView(data: MutableList<ResultBook>) {
        val recyclerView = findViewById<RecyclerView>(R.id.my_book_list)
        val adapter = BookListAdapter(this, data)
        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        val decorator = DividerItemDecoration(this, manager.orientation)
        recyclerView?.addItemDecoration(decorator)
    }

    private fun createDummyData(): MutableList<ResultBook> {
        val books = mutableListOf<ResultBook>()
        for (i in 1..10) {
            val id = "abc"
            val image =
                "http://books.google.com/books/content?id=13gDwgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"
            val title = "鬼滅の刃(${i})"
            val authors = arrayListOf("吾峠呼世晴", "TEST")
            books.add(ResultBook(id, title, authors, image))
        }
        return books
    }

    private fun setFabClickListener() {
        val fab: View = findViewById(R.id.fab_add_book)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, BookSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.toolbar_title)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }
}
