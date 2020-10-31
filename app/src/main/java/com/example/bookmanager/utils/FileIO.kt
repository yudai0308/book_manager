package com.example.bookmanager.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * ファイルの読み込み、書き込みを実行するためのクラス。
 */
class FileIO {

    companion object {
        fun readBookImage(context: Context, fileName: String): Drawable? {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_IMAGE, fileName)
                Drawable.createFromPath(file.path)
            } catch (e: IOException) {
                Log.e(null, "画像の読み込みに失敗しました。")
                null
            }
        }

        fun saveBookImage(context: Context, bitmap: Bitmap, fileName: String): Boolean {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_IMAGE, fileName)
                FileOutputStream(file).use {
                    return bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            } catch (e: IOException) {
                Log.e(null, "画像の保存に失敗しました。")
                false
            }
        }

        fun readReviewFile(context: Context, fileName: String): String? {
            val file = getFile(context, C.DIRECTORY_NAME_BOOK_REVIEW, fileName)

            if (!file.exists()) {
                return null
            }

            val builder = StringBuilder()
            file.forEachLine {
                if (it.isNotEmpty()) {
                    builder.append(it + "\n\n")
                }
            }

            return builder.toString()
        }

        fun saveReviewFile(context: Context, fileName: String, text: String): Boolean {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_REVIEW, fileName)
                file.writeText(text)
//                FileOutputStream(file).use {
//                    it.write(text.toByteArray())
//                }
                true
            } catch (e: IOException) {
                Log.e(null, "テキストファイルの保存に失敗しました。")
                false
            }
        }

        private fun getFile(context: Context, directoryName: String, fileName: String): File {
            val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getDir(
                directoryName,
                Context.MODE_PRIVATE
            )
            return File(directory, fileName)
        }
    }

}
