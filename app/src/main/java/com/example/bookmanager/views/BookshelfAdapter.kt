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
import com.example.bookmanager.utils.ImageIO
import kotlinx.coroutines.runBlocking

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
        val image = runBlocking { ImageIO.readBookImage(context, book.id) }

        holder.binding.bookshelfItemImage.apply {
            setImageDrawable(image)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }

//    private fun readFromInternalStorage(fileName: String): Drawable? {
//        return try {
//            val contextWrapper = ContextWrapper(context)
//            val directory = contextWrapper.getDir(
//                C.DIRECTORY_NAME_BOOK_IMAGE,
//                Context.MODE_PRIVATE
//            )
//            val path = File(directory, fileName)
//            Drawable.createFromPath(path.toString())
//        } catch (e: IOException) {
//            Log.e(null, "画像の読み込みに失敗しました。")
//            null
//        }
//    }

    fun update(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    inner class BookShelfHolder(val binding: ListItemBookshelfBinding) :
        RecyclerView.ViewHolder(binding.root)
}
