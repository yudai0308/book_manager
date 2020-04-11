package com.example.bookmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }

    private fun createDummyData(): MutableList<MyBook> {
        val list = mutableListOf<MyBook>()
        for (i in 1..10) {
            val image = R.mipmap.ic_launcher
            val title = "海辺のカフカ"
            val author = "村上春樹"
            list.add(MyBook(image, title, author))
        }
        return list
    }
}
