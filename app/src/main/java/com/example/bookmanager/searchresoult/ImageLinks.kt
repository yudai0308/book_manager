package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ImageLinks {
    var smallThumbnail: String? = null
    var thumbnail: String? = null
}