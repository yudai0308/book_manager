package com.example.bookmanager.views

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.RowBookListBinding
import com.example.bookmanager.models.Book
import com.example.bookmanager.utils.Libs

class BookListAdapter(
    private val activity: Activity,
    private var resultBooks: List<Book>,
    private val clickListener: View.OnClickListener? = null
) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val binding: RowBookListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_book_list,
            parent,
            false
        )
        binding.root.setOnClickListener(clickListener)
        return BookListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val resultBook = resultBooks[position]
        holder.binding.apply {
            lifecycleOwner = activity as LifecycleOwner
            bookListTitle.text = resultBook.title
            bookListAuthor.text = Libs.listToString(resultBook.authors)
        }
        Glide.with(activity)
            .load(resultBook.image)
            .into(holder.binding.bookListImage)
    }

    override fun getItemCount(): Int {
        return resultBooks.size
    }

    fun update(resultBooks: List<Book>) {
        this.resultBooks = resultBooks
        notifyDataSetChanged()
    }

    inner class BookListViewHolder(val binding: RowBookListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
