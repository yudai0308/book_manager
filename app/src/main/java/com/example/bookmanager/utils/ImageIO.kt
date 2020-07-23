package com.example.bookmanager.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageIO {

    companion object {
        fun readBookImage(context: Context, fileName: String): Drawable? {
            return try {
                val contextWrapper = ContextWrapper(context)
                val directory = contextWrapper.getDir(
                    C.DIRECTORY_NAME_BOOK_IMAGE,
                    Context.MODE_PRIVATE
                )
                val path = File(directory, fileName)
                Drawable.createFromPath(path.toString())
            } catch (e: IOException) {
                Log.e(null, "画像の読み込みに失敗しました。")
                null
            }
        }

        fun saveBookImage(context: Context, bitmap: Bitmap, fileName: String): Boolean {
            return try {
                val contextWrapper = ContextWrapper(context)
                val directory = contextWrapper.getDir(
                    C.DIRECTORY_NAME_BOOK_IMAGE,
                    Context.MODE_PRIVATE
                )
                val path = File(directory, fileName)
                FileOutputStream(path).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                true
            } catch (e: IOException) {
                Log.e(null, "画像の保存に失敗しました。")
                false
            }
        }
    }

}
