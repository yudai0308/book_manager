package com.example.bookmanager.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.views.BookSearchAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("resultBooks")
    fun RecyclerView.bindResultBooks(resultBooks: List<BookSearchResult>?) {
        resultBooks ?: return
        (adapter as BookSearchAdapter).update(resultBooks)
    }
}
