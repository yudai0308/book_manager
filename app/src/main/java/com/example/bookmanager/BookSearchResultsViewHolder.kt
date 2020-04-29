package com.example.bookmanager

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookSearchResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mBookTitle: TextView = itemView.findViewById(R.id.search_book_title)
    var mBookAuthor: TextView = itemView.findViewById(R.id.search_book_author)
    var mBookImage: ImageView = itemView.findViewById(R.id.search_book_image)
}
