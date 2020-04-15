package com.example.bookmanager

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyBookListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var myBookImage: ImageView = itemView.findViewById(R.id.my_book_image)
    var myBookTitle: TextView = itemView.findViewById(R.id.my_book_title)
    var myBookAuthors: TextView = itemView.findViewById(R.id.my_book_author)
}