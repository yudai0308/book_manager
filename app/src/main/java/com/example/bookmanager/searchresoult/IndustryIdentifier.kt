package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class IndustryIdentifier {
    var type: String? = null
    var identifier: String? = null
}