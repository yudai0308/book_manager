package com.example.bookmanager.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentBookReviewBinding
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import io.noties.markwon.Markwon

/**
 * 本詳細ページ内、感想タブのフラグメント。
 */
class BookReviewFragment : Fragment() {

    private var bookId = ""

    private var review = ""

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_book_review, container, false
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val markwon = context?.let { Markwon.create(it) }
        review = context?.let {
            FileIO.readReviewFile(it, bookId)
        } ?: ""

        if (markwon != null && review.isNotBlank()) {
            markwon.setMarkdown(binding.bookReviewText, review)
            binding.bookReviewText.visibility = View.VISIBLE
            binding.noBookReview.visibility = View.INVISIBLE
        } else {
            binding.noBookReview.visibility = View.VISIBLE
            binding.bookReviewText.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        binding.root.requestLayout()
        binding.root.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun getInstance(bookId: String) = BookReviewFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_ID, bookId)
            }
        }
    }
}
