package com.example.bookmanager

class Const {
    companion object {
        const val BOOK_SEARCH_API_URL = "https://www.googleapis.com/books/v1/volumes"
        // 検索方法
        const val SEARCH_FREE_WORD = "フリーワード検索"
        const val SEARCH_TITLE = "タイトル検索"
        const val SEARCH_AUTHOR = "著者名検索"
        // パラメータ
        const val PARAM_TITLE = "intitle"
        const val PARAM_AUTHOR = "inauthor"
    }
}