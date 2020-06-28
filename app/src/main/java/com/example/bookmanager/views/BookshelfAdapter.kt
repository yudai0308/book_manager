package com.example.bookmanager.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ListItemBookshelfBinding
import com.example.bookmanager.models.BookSearchResult

class BookshelfAdapter() : RecyclerView.Adapter<BookshelfAdapter.BookShelfHolder>() {

    lateinit var context: Context
    private val books: List<BookSearchResult> = fetchBooks()
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
        holder.binding.apply {
            titleBookshelfItem.text = book.title
        }
        Glide.with(context)
            .load(book.image)
            .transform(RoundedCorners(4))
            .into(holder.binding.imageBookshelfItem)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    /**
     * DB から本棚に登録されている本の情報を取得する。
     */
    private fun fetchBooks(): List<BookSearchResult> {
        return createDummyData()
    }

    private fun createDummyData(): MutableList<BookSearchResult> {
        val books = mutableListOf<BookSearchResult>()
        for (i in 1..10) {
            val id = "abc"
            val image =
                "http://books.google.com/books/content?id=13gDwgEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api"
            val title = "鬼滅の刃(${i})"
            val authors = arrayListOf("吾峠呼世晴", "TEST")
            books.add(BookSearchResult(id, title, authors, image))
        }
        return books
    }

    inner class BookShelfHolder(val binding: ListItemBookshelfBinding) :
        RecyclerView.ViewHolder(binding.root)
}
