package io.github.yudai0308.honma.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageLinks(
    var smallThumbnail: String? = null,
    var thumbnail: String? = null
)
