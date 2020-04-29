package com.example.bookmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookListAdapter(
    private val context: Context,
    private val listData: MutableList<Book>
) : RecyclerView.Adapter<BookListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_book_list, parent, false)
        return BookListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val item = listData[position]
        holder.mBookTitle.text = item.mTitle
        holder.mBookAuthor.text = Libs.listToString(item.mAuthors)
        Glide.with(context)
            .load(item.mImage)
            .into(holder.mBookImage)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun removeAll() {
        val cnt = listData.size
        listData.removeAll { true }
        for (i: Int in (cnt - 1)..0) {
            notifyItemRemoved(i)
        }
    }
}