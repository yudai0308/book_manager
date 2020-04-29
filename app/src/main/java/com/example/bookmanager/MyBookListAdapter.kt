package com.example.bookmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyBookListAdapter(private val listData: MutableList<MyBook>) :
    RecyclerView.Adapter<MyBookListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_my_book_list, parent, false)
        return MyBookListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBookListViewHolder, position: Int) {
        val item = listData[position]
        holder.myBookImage.setImageResource(item.mImage)
        holder.myBookTitle.text = item.mTitle
        holder.myBookAuthors.text = Libs.listToString(item.mAuthors)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
