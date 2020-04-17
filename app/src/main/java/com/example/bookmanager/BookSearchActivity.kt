package com.example.bookmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.MaterialSearchBar

class BookSearchActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        setUpSearchBar()
        val recyclerView = findViewById<RecyclerView>(R.id.search_book_results)
        val adapter = BookSearchResultsAdapter(createDummyData())
        val manager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = manager

        val decorator = DividerItemDecoration(this, manager.orientation)
        recyclerView.addItemDecoration(decorator)
    }

    private fun setUpSearchBar() {
        val searchBar = findViewById<MaterialSearchBar>(R.id.searchBar)
        searchBar.setOnSearchActionListener(this)
        searchBar.openSearch()
    }

    private fun createDummyData() : MutableList<NewBook> {
        val newBooks = mutableListOf<NewBook>()
        for (i in 1..10) {
            newBooks.add(NewBook(
                "進撃の巨人${i}",
                "諫山創",
                "巨人たちが襲いかかる！",
                "image.jpg")
            )
        }
        return newBooks
    }

    override fun onButtonClicked(buttonCode: Int) {}

    override fun onSearchStateChanged(enabled: Boolean) {}

    override fun onSearchConfirmed(text: CharSequence?) {}
}
