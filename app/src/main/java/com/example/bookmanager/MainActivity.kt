package com.example.bookmanager

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val books = createDummyData()
        setDataToRecyclerView(books)
        setFabClickListener()
        setUpToolbar()
    }

    private fun setDataToRecyclerView(data: MutableList<MyBook>) {
        val recyclerView = findViewById<RecyclerView>(R.id.rvMyBookList)
        val adapter = MyBookListAdapter(data)
        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        val decorator = DividerItemDecoration(this, manager.orientation)
        recyclerView?.addItemDecoration(decorator)
    }

    private fun createDummyData(): MutableList<MyBook> {
        val list = mutableListOf<MyBook>()
        for (i in 1..10) {
            val image = R.mipmap.ic_launcher
            val title = "鬼滅の刃(${i})"
            val authors = arrayListOf("吾峠呼世晴", "TEST")
            list.add(MyBook(image, title, authors))
        }
        return list
    }

    private fun setFabClickListener() {
        val fab: View = findViewById(R.id.fabAddBook)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, BookSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.toolbar_title)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }
}
