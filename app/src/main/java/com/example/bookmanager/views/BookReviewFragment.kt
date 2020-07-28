package com.example.bookmanager.views

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentBookReviewBinding
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO


class BookReviewFragment : Fragment() {

    private var bookId = ""

    private lateinit var binding: FragmentBookReviewBinding

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
            R.layout.fragment_book_review,
            container,
            false
        )

        val review = context?.let {
            FileIO.readReviewFile(it, bookId)
        }

        binding.bookReviewText.apply {
            text = review ?: getString(R.string.memo_not_found_message)
            if (review == null) {
                gravity = Gravity.CENTER
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(bookId: String) = BookReviewFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_ID, bookId)
            }
        }
    }
}
