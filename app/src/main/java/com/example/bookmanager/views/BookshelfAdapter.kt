package com.example.bookmanager.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ListItemBookshelfBinding
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.FileIO
import kotlinx.coroutines.runBlocking

/**
 * 本棚ページでリスト表示するためのアダプター。
 */
class BookshelfAdapter : RecyclerView.Adapter<BookshelfAdapter.BookShelfHolder>() {

    lateinit var context: Context

    private var books: List<Book> = listOf()

    private var listener: View.OnClickListener? = null

    fun setListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfHolder {
        context = parent.context
        val binding: ListItemBookshelfBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.list_item_bookshelf,
            parent,
            false
        )
        binding.root.setOnClickListener(listener)
        return BookShelfHolder(binding)
    }

    override fun onBindViewHolder(holder: BookShelfHolder, position: Int) {
        val book = books[position]
        val image = runBlocking { FileIO.readBookImage(context, book.id) }

        holder.binding.bookshelfItemImage.apply {
            if (image != null) {
                setImageDrawable(image)
            } else {
                holder.binding.bookshelfItemTitle.apply {
                    visibility = View.VISIBLE
                    text = book.title
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun update(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    class BookShelfHolder(val binding: ListItemBookshelfBinding) :
        RecyclerView.ViewHolder(binding.root)
}
