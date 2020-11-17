package com.example.bookmanager.utils

/**
 * 定数を管理するクラス。
 */
class C {
    companion object {

        const val BOOK_SEARCH_API_URL = "https://www.googleapis.com/books/v1/volumes"
        const val DB_NAME = "book_database"
        const val DIRECTORY_NAME_BOOK_IMAGE = "book_image"
        const val DIRECTORY_NAME_BOOK_REVIEW = "book_review"

        // FIXME: string.xml に移行。
        // 検索方法
        const val SEARCH_FREE_WORD = "フリーワード検索"
        const val SEARCH_TITLE = "タイトル検索"
        const val SEARCH_AUTHOR = "著者名検索"

        // パラメータ
        const val ADD_QUERY = "?q="
        const val PARAM_TITLE = "intitle:"
        const val PARAM_AUTHOR = "inauthor:"
        const val PARAM_MAX = "&maxResults="
        const val PARAM_INDEX = "&startIndex="
        const val MAX_RESULTS_COUNT = 30

        // FIXME: string.xml に移行。
        // 本検索ページ関連
        const val UNKNOWN = "Unknown"
        const val ADD_BOOK = "本棚に追加しました。"
        const val CONNECTION_FAILURE_MSG = "通信に失敗しました。\n検索をやり直してください。"

        // DialogFragment タグ
        const val DIALOG_TAG_ADD_BOOK = "add_book_dialog"
        const val DIALOG_TAG_CANCEL_EDITING = "cancel_editing_dialog"
        const val DIALOG_TAG_DELETE_BOOK = "delete_book_dialog"

        // bundle キー
        const val BOOK_ID = "bookId"
        const val BOOK_DESCRIPTION = "book_description"
    }
}
