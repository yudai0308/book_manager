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
import com.example.bookmanager.utils.ViewUtil
import kotlinx.coroutines.runBlocking

/**
 * 本棚ページでリスト表示するためのアダプター。
 */
class BookshelfAdapter : RecyclerView.Adapter<BookshelfAdapter.BookShelfHolder>() {

    lateinit var context: Context

    private var books: List<Book> = listOf()

    private var onClickListener: View.OnClickListener? = null

    private var onBindListener: OnBindViewHolderListener? = null

    interface OnBindViewHolderListener {
        fun onBound(view: View)
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        onClickListener = listener
    }

    fun setOnBindViewHolderListener(listener: OnBindViewHolderListener) {
        onBindListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookShelfHolder {
        context = parent.context
        val binding: ListItemBookshelfBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.list_item_bookshelf, parent, false
        )
//        binding.root.setOnClickListener(onClickListener)
        return BookShelfHolder(binding)
    }

    override fun onBindViewHolder(holder: BookShelfHolder, position: Int) {
        val book = books[position]
        val image = runBlocking { FileIO.readBookImage(context, book.id) }

        val parentTotalPaddingPx = ViewUtil.dpToPx(context, 8)
        val itemTotalMarginPx = ViewUtil.dpToPx(context, 8)
        val itemWidth = getImageWidth(3, itemTotalMarginPx, parentTotalPaddingPx)
        if (image != null) {
            holder.binding.bookshelfItemCover.also {
                it.visibility = View.VISIBLE
                it.setImageDrawable(image)
                it.setOnClickListener(onClickListener)
                it.layoutParams.width = itemWidth
                onBindListener?.onBound(it)
            }
            holder.binding.bookshelfItemTitle.visibility = View.GONE
        } else {
            holder.binding.bookshelfItemTitle.also {
                it.visibility = View.VISIBLE
                it.text = book.title
                it.setOnClickListener(onClickListener)
                it.layoutParams.width = itemWidth
                onBindListener?.onBound(it)
            }
            holder.binding.bookshelfItemCover.visibility = View.GONE
        }
    }

    private fun getImageWidth(columnCount: Int, itemTotalMarginPx: Int, parentTotalPaddingPx: Int): Int {
        val displayWidth = ViewUtil.getDisplayWidth(context)
        val eachItemWidth = (displayWidth - parentTotalPaddingPx) / columnCount
        return eachItemWidth - itemTotalMarginPx
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
