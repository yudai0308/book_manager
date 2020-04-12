package com.example.bookmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rvMyBookList)
        val adapter = MyBookListAdapter(createDummyData())
        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        val decorator = DividerItemDecoration(this, manager.orientation)
        recyclerView?.addItemDecoration(decorator)

        val fab: View = findViewById(R.id.fabAddBook)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, BookSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createDummyData(): MutableList<MyBook> {
        val list = mutableListOf<MyBook>()
        for (i in 1..10) {
            val image = R.mipmap.ic_launcher
            val title = "鬼滅の刃(${i})"
            val author = "吾峠呼世晴"
            list.add(MyBook(image, title, author))
        }
        return list
    }
}
