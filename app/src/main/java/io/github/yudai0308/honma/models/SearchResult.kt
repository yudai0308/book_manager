package io.github.yudai0308.honma.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    var totalItems: Int,
    var items: List<Item>?
)
