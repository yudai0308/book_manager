package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VolumeInfo(
    var title: String?,
    var authors: List<String>?,
    var description: String?,
    var imageLinks: ImageLinks?
) {
}