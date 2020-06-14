package com.example.bookmanager.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.models.ResultBook
import com.example.bookmanager.views.BookListAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("resultBooks")
    fun RecyclerView.bindResultBooks(resultBooks: List<ResultBook>?) {
        resultBooks ?: return
        (adapter as BookListAdapter).update(resultBooks)
    }
}
