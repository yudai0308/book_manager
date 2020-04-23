package com.example.bookmanager.searchresoult

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Item(var id: String, var volumeInfo: VolumeInfo?)