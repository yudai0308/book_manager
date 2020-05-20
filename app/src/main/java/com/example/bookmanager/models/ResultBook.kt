package com.example.bookmanager.models

data class ResultBook (
    val id: String,
    val title: String,
    val authors: List<String>,
    val image: String
)
