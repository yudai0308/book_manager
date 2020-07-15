package com.example.bookmanager.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.views.BookSearchAdapter
import com.example.bookmanager.views.BookshelfAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("resultBooks")
    fun RecyclerView.bindResultBooks(resultBooks: List<BookSearchResult>?) {
        if (resultBooks == null || adapter == null) {
            return
        }

        (adapter as BookSearchAdapter).update(resultBooks)
    }

    @JvmStatic
    @BindingAdapter("books")
    fun RecyclerView.bindingBooks(books: List<Book>?) {
        if (books == null || adapter == null) {
            return
        }

        (adapter as BookshelfAdapter).update(books)
    }
}
