package com.example.bookmanager.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Item(var id: String, var volumeInfo: VolumeInfo?)
