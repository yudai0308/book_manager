package io.github.yudai0308.honma.views

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * 本詳細ページのタブを作成するためのアダプター。
 */
class BookDetailPagerAdapter(
    activity: AppCompatActivity,
    private val bookDataFragment: BookDataFragment,
    private val bookReviewFragment: BookReviewFragment
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = Tab.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            Tab.BOOK_DATA.position -> bookDataFragment
            Tab.BOOK_REVIEW.position -> bookReviewFragment
            else -> throw IllegalArgumentException()
        }
    }

    enum class Tab(val position: Int) {
        BOOK_DATA(0),
        BOOK_REVIEW(1)
    }
}
