package com.example.bookmanager.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.models.ResultBook
import com.example.bookmanager.utils.Libs

class BookListAdapter(
    private val context: Context,
    private val listData: MutableList<ResultBook>,
    private val clickListener: View.OnClickListener? = null
) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        view = inflater.inflate(R.layout.row_book_list, parent, false)

        if (clickListener != null) {
            view.setOnClickListener(clickListener)
        }

        return BookListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val item = listData[position]
        holder.bookTitle.text = item.title
        holder.bookAuthor.text = Libs.listToString(item.authors)
        Glide.with(context)
            .load(item.image)
            .into(holder.bookImage)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun removeAll() {
        val cnt = listData.size
        listData.removeAll { true }
        for (i: Int in (cnt - 1) downTo  0) {
            notifyItemRemoved(i)
        }
    }

    inner class BookListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookTitle: TextView = itemView.findViewById(R.id.book_list_title)
        var bookAuthor: TextView = itemView.findViewById(R.id.book_list_author)
        var bookImage: ImageView = itemView.findViewById(R.id.book_list_image)
    }
}
