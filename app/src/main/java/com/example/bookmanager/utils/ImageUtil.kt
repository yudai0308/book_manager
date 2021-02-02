package com.example.bookmanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

class ImageUtil {
    companion object {
        fun getBitmapWithUri(context: Context, uri: Uri): Bitmap {
            return if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        }

        fun calculateBitmapSize(bitmap: Bitmap): Int {
            return ByteArrayOutputStream().let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.toByteArray().size
            }
        }
    }
}