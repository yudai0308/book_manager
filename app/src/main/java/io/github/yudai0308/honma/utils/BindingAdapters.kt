package io.github.yudai0308.honma.utils

import android.widget.Button
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.yudai0308.honma.R
import io.github.yudai0308.honma.models.BookSearchResult
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.views.BookSearchAdapter
import io.github.yudai0308.honma.views.BookshelfAdapter

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

    @JvmStatic
    @BindingAdapter("book_status")
    fun Button.bindStatusCode(statusCode: Int?) {
        statusCode ?: return

        if (statusCode == Book.Status.WANT_TO_READ.code && id == R.id.status_button_want_to_read) {
            setBackgroundResource(R.drawable.bg_filter_btn_selected)
            return
        }
        if (statusCode == Book.Status.READING.code && id == R.id.status_button_reading) {
            setBackgroundResource(R.drawable.bg_filter_btn_selected)
            return
        }
        if (statusCode == Book.Status.FINISHED.code && id == R.id.status_button_finished) {
            setBackgroundResource(R.drawable.bg_filter_btn_selected)
            return
        }

        setBackgroundResource(R.drawable.bg_filter_btn_selectable)
    }
}
