package com.example.bookmanager.models

data class BookSearchResult(
    val itemCount: Int,
    val items: List<BookSearchResultItem>
)
