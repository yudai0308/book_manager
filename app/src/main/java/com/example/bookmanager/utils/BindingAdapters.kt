package com.example.bookmanager.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.views.BookSearchAdapter
import com.example.bookmanager.views.BookshelfAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("search_result")
    fun RecyclerView.bindResult(result: BookSearchResult?) {
        if (result == null || adapter == null) {
            return
        }

        (adapter as BookSearchAdapter).update(result)
    }

    @JvmStatic
    @BindingAdapter("my_books")
    fun RecyclerView.bindMyBooks(books: List<Book>?) {
        if (books == null || adapter == null) {
            return
        }

        (adapter as BookshelfAdapter).update(books)
    }
}
