package com.example.bookmanager.views

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.bookmanager.databinding.ListItemBookSearchBottomBinding
import com.example.bookmanager.models.BookSearchResult
import com.example.bookmanager.models.BookSearchResultItem
import com.example.bookmanager.rooms.common.BookRepository
import com.example.bookmanager.utils.StringUtil

/**
 * 本の検索結果をリスト表示するためのアダプター。
 */
class BookSearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private var bookSearchResult: BookSearchResult = BookSearchResult(0, listOf())

    private var itemClickListener: View.OnClickListener? = null

    fun setOnItemClickListener(listener: View.OnClickListener) {
        this.itemClickListener = listener
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
                binding.root.setOnClickListener(itemClickListener)
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
            bookSearchItemTitle.text = resultItem.title
            bookSearchItemAuthor.text = StringUtil.listToString(resultItem.authors)
            bookSearchRating.rating = resultItem.averageRating ?: 0F
            bookSearchRatingsCount.text = if (resultItem.ratingsCount > 0) {
                resultItem.ratingsCount.toString()
            } else {
                context.getString(R.string.hyphen)
            }
            if (resultItem.infoLink.isNotBlank()) {
                bookSearchDetailLinkButton.setOnClickListener {
                    val uri = Uri.parse(resultItem.infoLink)
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            } else {
                bookSearchDetailLinkButton.visibility = View.GONE
            }
            // TODO: リポジトリは ViewModel 経由で操作したい
            val repository = BookRepository(context)
            bookSearchBookmark.visibility = if (repository.exists(resultItem.id)) {
                View.VISIBLE
            } else {
                View.INVISIBLE
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

    fun update(result: BookSearchResult) {
        bookSearchResult = result
        notifyDataSetChanged()
    }

    class BookSearchViewHolder(val binding: ListItemBookSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    class BookSearchBottomViewHolder(val binding: ListItemBookSearchBottomBinding) :
        RecyclerView.ViewHolder(binding.root)
}
