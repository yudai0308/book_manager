package io.github.yudai0308.honma.models

import io.github.yudai0308.honma.rooms.entities.Book

data class BookSortCondition(
    var column: Book.Column,
    var isAsc: Boolean
)
