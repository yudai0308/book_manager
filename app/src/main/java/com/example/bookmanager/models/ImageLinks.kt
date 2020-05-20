package com.example.bookmanager.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageLinks(
    var smallThumbnail: String? = null,
    var thumbnail: String? = null
)
