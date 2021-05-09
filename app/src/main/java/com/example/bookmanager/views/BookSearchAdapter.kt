package com.example.bookmanager.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ListItemBookSearchBinding
import com.example.bookmanager.databinding.ListItemBookSearchBottomBinding
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.models.BookSearchResultItem

/**
 * 本の検索結果をリスト表示するためのアダプター。
 */
class BookSearchAdapter(private val listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private var bookSearchResult: BookSearchResult = BookSearchResult(0, listOf())

    interface ClickListener {
        fun getOnAddButtonClickListener(item: BookSearchResultItem): View.OnClickListener
        fun getOnDetailButtonClickListener(item: BookSearchResultItem): View.OnClickListener
    }

    companion object {
        const val BOOK_RESULT_LIST_ITEM = 0
        const val BOOK_RESULT_LIST_BOTTOM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            BOOK_RESULT_LIST_ITEM -> {
                val binding: ListItemBookSearchBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_book_search,
                    parent,
                    false
                )
                BookSearchViewHolder(binding)
            }
            BOOK_RESULT_LIST_BOTTOM -> {
                val binding: ListItemBookSearchBottomBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_book_search_bottom,
                    parent,
                    false
                )
                BookSearchBottomViewHolder(binding)
            }
            else -> throw Exception("Invalid view type is detected.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            BOOK_RESULT_LIST_ITEM -> {
                val item = bookSearchResult.items[position]
                setDataToBookSearchViewHolder(item, holder as BookSearchViewHolder)
            }
            BOOK_RESULT_LIST_BOTTOM -> {
                // Do nothing.
            }
        }
    }

    override fun getItemCount(): Int {
        return when {
            // 検索結果が 0 件、または検索されていない場合
            bookSearchResult.itemCount == 0 -> {
                0
            }
            // すべて検索し終えた場合
            bookSearchResult.items.size == bookSearchResult.itemCount -> {
                bookSearchResult.items.size
            }
            // 上記以外の場合（ProgressBar を表示させるための行を追加する）
            else -> {
                bookSearchResult.items.size + 1
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            bookSearchResult.items.size -> BOOK_RESULT_LIST_BOTTOM
            else -> BOOK_RESULT_LIST_ITEM
        }
    }

    private fun setDataToBookSearchViewHolder(
        resultItem: BookSearchResultItem,
        holder: BookSearchViewHolder
    ) {
        holder.binding.apply {
            lifecycleOwner = context as LifecycleOwner
            item = resultItem
            bookSearchAddButton.setOnClickListener(
                listener.getOnAddButtonClickListener(resultItem)
            )
            if (resultItem.infoLink.isNotBlank()) {
                bookSearchDetailLinkButton.setOnClickListener(
                    listener.getOnDetailButtonClickListener(resultItem)
                )
            }
        }
        if (resultItem.image.isNotBlank()) {
            Glide.with(context)
                .load(resultItem.image)
                .placeholder(R.drawable.now_loading)
                .into(holder.binding.bookSearchItemImage)
        } else {
            holder.binding.bookSearchItemImage.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.no_image)
            )
        }
    }

    fun update(newResult: BookSearchResult) {
        val oldResult = bookSearchResult
        bookSearchResult = newResult
        val diffResult = DiffUtil.calculateDiff(SearchResultDiffCallback(oldResult, newResult))
        diffResult.dispatchUpdatesTo(this)
    }

    private class BookSearchViewHolder(val binding: ListItemBookSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class BookSearchBottomViewHolder(binding: ListItemBookSearchBottomBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SearchResultDiffCallback(
        val oldResult: BookSearchResult,
        val newResult: BookSearchResult
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldResult.items[oldItemPosition]
            val new = newResult.items[newItemPosition]
            return old.id == new.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldResult.items[oldItemPosition]
            val new = newResult.items[newItemPosition]
            return old == new
        }

        override fun getOldListSize() = oldResult.items.size

        override fun getNewListSize() = newResult.items.size
    }
}
