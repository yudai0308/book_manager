package com.example.bookmanager.views

import android.content.Context
import android.content.Intent
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

    private val sharedPref by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }

    private val markdownMode: Boolean
        get() {
            return sharedPref?.getBoolean(MARKDOWN_MODE, true) ?: true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookId = arguments?.getString(C.BOOK_ID) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_book_review, container, false
        )

        binding.apply {
            addReviewButton.setOnClickListener { startBookReviewEditingActivity() }
            editReviewButton.setOnClickListener { startBookReviewEditingActivity() }
            markdownInvisibleIcon.setOnClickListener { switchMarkdownMode(true) }
            markdownVisibleIcon.setOnClickListener { switchMarkdownMode(false) }
        }

        switchMarkdownMode(markdownMode)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val markwon = context?.let { Markwon.create(it) }
        review = context?.let {
            FileIO.readReviewFile(it, bookId)
        } ?: ""

        if (review.isNotBlank()) {
            markwon?.setMarkdown(binding.bookReviewMarkdown, review)
            binding.bookReviewSimpleText.text = review
            binding.bookReview.visibility = View.VISIBLE
            binding.noBookReview.visibility = View.GONE
        } else {
            binding.noBookReview.visibility = View.VISIBLE
            binding.bookReview.visibility = View.GONE
        }

        binding.root.requestLayout()
    }

    private fun startBookReviewEditingActivity() {
        startActivity(Intent(context, BookReviewEditingActivity::class.java).apply {
            putExtra(C.BOOK_ID, bookId)
        })
    }

    private fun switchMarkdownMode(isEnabled: Boolean) {
        binding.apply {
            if (isEnabled) {
                switchOnMarkdownMode()
            } else {
                switchOffMarkdownMode()
            }
        }
    }

    private fun switchOnMarkdownMode() {
        binding.apply {
            markdownVisibleIcon.visibility = View.VISIBLE
            markdownInvisibleIcon.visibility = View.INVISIBLE
            bookReviewMarkdown.visibility = View.VISIBLE
            bookReviewSimpleText.visibility = View.GONE
        }
        sharedPref?.edit()?.apply {
            putBoolean(MARKDOWN_MODE, true)
            apply()
        }
    }

    private fun switchOffMarkdownMode() {
        binding.apply {
            markdownVisibleIcon.visibility = View.INVISIBLE
            markdownInvisibleIcon.visibility = View.VISIBLE
            bookReviewMarkdown.visibility = View.GONE
            bookReviewSimpleText.visibility = View.VISIBLE
        }
        sharedPref?.edit()?.apply {
            putBoolean(MARKDOWN_MODE, false)
            apply()
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(bookId: String) = BookReviewFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_ID, bookId)
            }
        }

        private const val MARKDOWN_MODE = "markdown_mode"
    }
}
