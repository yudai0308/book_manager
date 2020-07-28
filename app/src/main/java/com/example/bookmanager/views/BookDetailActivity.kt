package com.example.bookmanager.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookDetailBinding
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.BookInfo
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.Libs
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.runBlocking

class BookDetailActivity : AppCompatActivity() {

    private val binding: ActivityBookDetailBinding by lazy {
        DataBindingUtil.setContentView<ActivityBookDetailBinding>(
            this,
            R.layout.activity_book_detail
        )
    }

    private val bookId: String by lazy { intent.getStringExtra(C.BOOK_ID) }

    private val bookInfo by lazy { loadBookInfo(bookId) }

    private val bookImage by lazy {
        runBlocking {
            FileIO.readBookImage(this@BookDetailActivity, bookId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val description = getBookDescriptionFromBookInfo(bookInfo)
        val bookDescriptionFragment = BookDescriptionFragment.newInstance(description)
        val bookReviewFragment = BookReviewFragment.newInstance(bookId)
        val viewPager = binding.bookDetailViewPager.apply {
            adapter = BookDetailPagerAdapter(
                this@BookDetailActivity,
                bookDescriptionFragment,
                bookReviewFragment
            )
        }

        val tabLayout = binding.bookDetailTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                BookDetailPagerAdapter.BookDetailPage.BOOK_DESCRIPTION.position -> {
                    getString(R.string.book_detail_tab_description)
                }
                BookDetailPagerAdapter.BookDetailPage.BOOK_REVIEW.position -> {
                    getString(R.string.book_detail_tab_memo)
                }
                else -> throw IllegalArgumentException()
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        val bookTitle = bookInfo?.book?.title ?: ""
        val authors = bookInfo?.authors?.map { it.name }
        val authorsString =
            authors?.let { Libs.listToString(it) } ?: getString(R.string.unknown_author)
        binding.apply {
            bookDetailTitle.text = bookTitle
            bookDetailAuthor.text = authorsString
            bookDetailImage.setImageDrawable(bookImage)
        }
    }

    private fun loadBookInfo(bookId: String): BookInfo? {
        val db = Room.databaseBuilder(this, BookDatabase::class.java, C.DB_NAME).build()

        val bookDao = db.bookDao()
        return runBlocking { bookDao.loadBookInfoById(bookId) }
    }

    private fun getBookDescriptionFromBookInfo(bookInfo: BookInfo?): String {
        val description = bookInfo?.book?.description
        return if (description.isNullOrBlank()) {
            getString(R.string.book_description_not_found)
        } else {
            description
        }
    }
}
