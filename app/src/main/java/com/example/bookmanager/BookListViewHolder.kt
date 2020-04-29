package com.example.bookmanager

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mBookTitle: TextView = itemView.findViewById(R.id.book_list_title)
    var mBookAuthor: TextView = itemView.findViewById(R.id.book_list_author)
    var mBookImage: ImageView = itemView.findViewById(R.id.book_list_image)
}
