package com.example.bookmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A simple [Fragment] subclass.
 */
class BookshelfFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookshelf, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMyBookList)
        val adapter = MyBookListAdapter(createDummyData())
        val manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        val decorator = DividerItemDecoration(context, manager.orientation)
        recyclerView?.addItemDecoration(decorator)

        return view
    }

    private fun createDummyData() : MutableList<MyBook> {
        val list = mutableListOf<MyBook>()
        for (i in 1..10) {
            val image = R.mipmap.ic_launcher
            val title = "海辺のカフカ"
            val author = "村上春樹"
            list.add(MyBook(image, title, author))
        }
        return list
    }
}
