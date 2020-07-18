package com.example.bookmanager.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookReviewBinding
import io.noties.markwon.Markwon

class BookReviewActivity : AppCompatActivity() {

    val binding: ActivityBookReviewBinding by lazy {
        DataBindingUtil.setContentView<ActivityBookReviewBinding>(
            this,
            R.layout.activity_book_review
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_review)

        setOnSaveClickListener()
    }

    private fun setOnSaveClickListener() {
        binding.saveButtonBookReview.setOnClickListener {
            val memo = binding.editorBookReview.text.toString()
            val markwon = Markwon.create(this)
        }
    }
}
