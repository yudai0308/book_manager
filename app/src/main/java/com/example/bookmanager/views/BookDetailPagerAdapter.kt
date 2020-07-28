package com.example.bookmanager.views

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BookDetailPagerAdapter(
    activity: AppCompatActivity,
    private val bookDescriptionFragment: BookDescriptionFragment,
    private val bookReviewFragment: BookReviewFragment
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = BookDetailPage.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            BookDetailPage.BOOK_DESCRIPTION.position -> bookDescriptionFragment
            BookDetailPage.BOOK_REVIEW.position -> bookReviewFragment
            else -> throw IllegalArgumentException()
        }
    }

    enum class BookDetailPage(val position: Int) {
        BOOK_DESCRIPTION(0),
        BOOK_REVIEW(1)
    }
}
