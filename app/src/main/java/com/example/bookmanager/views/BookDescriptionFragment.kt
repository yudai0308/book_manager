package com.example.bookmanager.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentBookDescriptionBinding
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.BookInfo
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.ImageIO
import com.example.bookmanager.utils.Libs
import kotlinx.coroutines.runBlocking

class BookDescriptionFragment : Fragment() {

    private lateinit var bookId: String

    private lateinit var binding: FragmentBookDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.getString(C.BOOK_ID) != null) {
                bookId = it.getString(C.BOOK_ID) as String
            } else {
                // TODO: パラメーターが null だった場合の処理。
                activity?.finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_book_description,
            container,
            false
        )
        val bookInfo = loadBook()
        showBookInformation(bookInfo as BookInfo)
        return binding.root
    }

    private fun loadBook(): BookInfo? {
        val db = context?.let {
            Room.databaseBuilder(it, BookDatabase::class.java, C.DB_NAME).build()
        }
        val bookDao = db?.bookDao()
        return runBlocking { bookDao?.loadBookInfoById(bookId) }
    }

    private fun showBookInformation(bookInfo: BookInfo) {
        val authorList = bookInfo.authors.map { it.name }
        val image = context?.let { ImageIO.readBookImage(it, bookInfo.book.id) }
        binding.apply {
            bookReviewTitle.text = bookInfo.book.title
            bookReviewAuthor.text = Libs.listToString(authorList)
            bookReviewSummary.text = bookInfo.book.description
            bookReviewImage.setImageDrawable(image)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bookId: String) = BookDescriptionFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_ID, bookId)
            }
        }
    }
}
