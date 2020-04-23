package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SearchResult(var totalItems: Int, var items: List<Item>?)