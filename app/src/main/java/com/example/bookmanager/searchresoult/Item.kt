package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Item {
    var kind: String? = null
    var id: String? = null
    var etag: String? = null
    var selfLink: String? = null
    var volumeInfo: VolumeInfo? = null
}