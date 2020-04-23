package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SearchResult {
    var kind: String? = null
    var totalItems: Int? = null
    var items: List<Item>? = null
}