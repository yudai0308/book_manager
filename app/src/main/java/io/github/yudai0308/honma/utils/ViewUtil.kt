package io.github.yudai0308.honma.utils

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class ViewUtil {
    companion object {
        fun showSnackBarLong(view: View, msg: String) {
            try {
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                return
            }
        }

        fun showToastLong(context: Context, text: String) {
            try {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                return
            }
        }

        fun getDisplayWidth(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val point = Point().also {
                display.getRealSize(it)
            }
            return point.x
        }

        fun getDisplayHeight(context: Context): Int {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val point = Point().also {
                display.getRealSize(it)
            }
            return point.y
        }

        fun dpToPx(context: Context, dp: Int): Int {
            val metrics = context.resources.displayMetrics
            return (dp * metrics.density).toInt()
        }

        fun pxToDp(context: Context, px: Int): Int {
            val metrics = context.resources.displayMetrics
            return (px / metrics.density).toInt()
        }
    }
}