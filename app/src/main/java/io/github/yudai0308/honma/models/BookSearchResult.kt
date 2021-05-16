package io.github.yudai0308.honma.models

data class BookSearchResult(
    val itemCount: Int,
    val items: List<BookSearchResultItem>
)
