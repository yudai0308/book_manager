package com.example.bookmanager.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    var totalItems: Int,
    var items: List<Item>?
)
