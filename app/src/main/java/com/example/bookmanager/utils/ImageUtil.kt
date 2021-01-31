package com.example.bookmanager.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class ImageUtil {
    companion object {
        fun calculateBitmapSize(bitmap: Bitmap): Int {
            return ByteArrayOutputStream().let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.toByteArray().size
            }
        }
    }
}