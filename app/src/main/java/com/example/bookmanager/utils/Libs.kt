package com.example.bookmanager.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class Libs {
    companion object {
        fun listToString(list: List<String>, divider: String = ", "): String {
            var returnVal = ""
            val count = list.size
            list.forEachIndexed { index: Int, string: String ->
                val add = if (index == count - 1) string else string + divider
                returnVal += add
            }
            return returnVal
        }

        fun showSnackBar(view: View, msg: String) {
            Snackbar.make(
                view,
                msg,
                Snackbar.LENGTH_LONG
            ).show()
        }

        fun showToastLong(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }
    }
}
