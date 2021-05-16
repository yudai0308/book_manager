package io.github.yudai0308.honma.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VolumeInfo(
    var title: String?,
    var authors: List<String>?,
    var publishedDate: String?,
    var averageRating: Float?,
    var ratingsCount: Int?,
    var description: String?,
    var imageLinks: ImageLinks?,
    var infoLink: String?
)
