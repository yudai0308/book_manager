package com.example.bookmanager.models

data class BookSearchResult (
    val id: String,
    val title: String,
    val authors: List<String>,
    val description: String,
    val image: String
)
