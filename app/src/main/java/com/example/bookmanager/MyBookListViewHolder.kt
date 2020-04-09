package com.example.bookmanager

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyBookListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var ivMyBookImage: ImageView = itemView.findViewById(R.id.ivMyBookImage)
    var tvMyBookTitle: TextView = itemView.findViewById(R.id.tvMyBookTitle)
    var tvMyBookAuthor: TextView = itemView.findViewById(R.id.tvMyBookAuthor)
}