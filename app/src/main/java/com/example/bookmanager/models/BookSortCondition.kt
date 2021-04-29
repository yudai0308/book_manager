package com.example.bookmanager.models

import com.example.bookmanager.rooms.entities.Book

data class BookSortCondition(
    var column: Book.Column,
    var isAsc: Boolean
)
