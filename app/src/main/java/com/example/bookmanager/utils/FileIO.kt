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
        private val TAG = this::class.simpleName

        fun readBookImage(context: Context, bookId: String): Drawable? {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_IMAGE, bookId)
                val image = Drawable.createFromPath(file.path)
                Log.i(TAG, "本の画像を読み込みました / bookId: $bookId")
                image
            } catch (e: IOException) {
                Log.e(TAG, "本の画像の読み込みに失敗しました / bookId: $bookId")
                null
            }
        }

        fun saveBookImage(context: Context, bitmap: Bitmap, bookId: String): Boolean {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_IMAGE, bookId)
                FileOutputStream(file).use {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) {
                        Log.i(TAG, "本の画像を保存しました / bookId: $bookId")
                        true
                    } else {
                        Log.e(TAG, "本の画像の保存に失敗しました / bookId: $bookId")
                        false
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "画像の保存に失敗しました / bookId: $bookId")
                false
            }
        }

        fun deleteBookImage(context: Context, bookId: String): Boolean {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_IMAGE, bookId)
                if (file.delete()) {
                    Log.i(TAG, "画像を削除しました / bookId: $bookId")
                    true
                } else {
                    Log.e(TAG, "画像の削除に失敗しました / bookId: $bookId")
                    false
                }
            } catch (e: IOException) {
                Log.e(TAG, "画像の削除に失敗しました / bookId: $bookId")
                false
            }
        }

        fun readReviewFile(context: Context, fileName: String): String? {
            val file = getFile(context, C.DIRECTORY_NAME_BOOK_REVIEW, fileName)

            if (!file.exists()) {
                return null
            }

            val builder = StringBuilder()
            file.forEachLine { builder.append(it + "\n") }

            return builder.toString()
        }

        fun saveReviewFile(context: Context, bookId: String, text: String): Boolean {
            return try {
                val file = getFile(context, C.DIRECTORY_NAME_BOOK_REVIEW, bookId)
                file.writeText(text)
                Log.i(TAG, "レビューファイルを保存しました / bookId: $bookId")
                true
            } catch (e: IOException) {
                Log.e(TAG, "レビューファイルの保存に失敗しました / bookId: $bookId")
                false
            }
        }

        private fun getFile(context: Context, directoryName: String, fileName: String): File {
            val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE)
            return File(directory, fileName)
        }
    }
}
