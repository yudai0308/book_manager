package com.example.bookmanager.views

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.databinding.ListItemBookshelfBinding

class BookshelfAdapter() : RecyclerView.Adapter<BookshelfAdapter.BookShelfHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BookShelfHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class BookShelfHolder(binding: ListItemBookshelfBinding) :
        RecyclerView.ViewHolder(binding.root)
}
