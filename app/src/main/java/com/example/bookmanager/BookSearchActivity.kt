package com.example.bookmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mancj.materialsearchbar.MaterialSearchBar

class BookSearchActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        setUpSearchBar()
    }

    private fun setUpSearchBar() {
        val searchBar = findViewById<MaterialSearchBar>(R.id.searchBar)
        searchBar.setOnSearchActionListener(this)
        searchBar.openSearch()
    }

    override fun onButtonClicked(buttonCode: Int) {}

    override fun onSearchStateChanged(enabled: Boolean) {}

    override fun onSearchConfirmed(text: CharSequence?) {}
}
