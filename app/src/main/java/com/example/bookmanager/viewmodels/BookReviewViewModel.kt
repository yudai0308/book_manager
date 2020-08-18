package com.example.bookmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bookmanager.utils.FileIO

class BookReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    var reviewContent: String = ""

    fun readReviewContent(bookId: String) {
        reviewContent = FileIO.readReviewFile(context, bookId) ?: return
    }
}
