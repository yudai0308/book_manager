package com.example.bookmanager.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ListItemBookSearchBinding
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.utils.Libs

/**
 * 本の検索結果をリスト表示するためのアダプター。
 */
class BookSearchAdapter : RecyclerView.Adapter<BookSearchAdapter.BookSearchViewHolder>() {

    private lateinit var context: Context

    private var resultBooks: List<BookSearchResult> = listOf()

    private var listener: View.OnClickListener? = null

    fun setListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchViewHolder {
        context = parent.context
        val binding: ListItemBookSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_book_search,
            parent,
            false
        )
        binding.root.setOnClickListener(listener)
        return BookSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookSearchViewHolder, position: Int) {
        val resultBook = resultBooks[position]
        holder.binding.apply {
            lifecycleOwner = context as LifecycleOwner
            bookSearchItemTitle.text = resultBook.title
            bookSearchItemAuthor.text = Libs.listToString(resultBook.authors)
            bookSearchRating.rating = resultBook.averageRating ?: 0F
            bookSearchRatingsCount.text = if (resultBook.ratingsCount > 0) {
                resultBook.ratingsCount.toString()
            } else {
                context.getString(R.string.hyphen)
            }
        }
        if (resultBook.image.isBlank()) {
            holder.binding.bookSearchItemImage.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.no_image)
            )
        } else {
            Glide.with(context)
                .load(resultBook.image)
                .placeholder(R.drawable.now_loading)
                .into(holder.binding.bookSearchItemImage)
        }
    }

    override fun getItemCount(): Int {
        return resultBooks.size
    }

    fun update(resultBooks: List<BookSearchResult>) {
        this.resultBooks = resultBooks
        notifyDataSetChanged()
    }

    class BookSearchViewHolder(val binding: ListItemBookSearchBinding) :
        RecyclerView.ViewHolder(binding.root)
}
