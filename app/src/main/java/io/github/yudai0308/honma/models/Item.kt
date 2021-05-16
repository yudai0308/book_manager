package io.github.yudai0308.honma.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Item(
    var id: String,
    var volumeInfo: VolumeInfo?
)
