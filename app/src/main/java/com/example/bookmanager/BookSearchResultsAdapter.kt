package com.example.bookmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BookSearchResultsAdapter(private val listData: MutableList<NewBook>) :
    RecyclerView.Adapter<BookSearchResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchResultsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_search_book, parent, false)
        return BookSearchResultsViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookSearchResultsViewHolder, position: Int) {
        val item = listData[position]
        holder.mBookTitle.text = item.mTitle
        holder.mBookAuthor.text = Libs.listToString(item.mAuthors)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun removeAll() {
        listData.removeAll { true }
    }

}