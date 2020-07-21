package com.example.bookmanager.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookDetailBinding
import com.example.bookmanager.utils.C

class BookDetailActivity : AppCompatActivity() {

    val binding: ActivityBookDetailBinding by lazy {
        DataBindingUtil.setContentView<ActivityBookDetailBinding>(
            this,
            R.layout.activity_book_detail
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookId = intent.getStringExtra(C.BOOK_ID)
        val fragment = BookDescriptionFragment.newInstance(bookId)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.book_review_root, fragment)
            commit()
        }
    }
}
