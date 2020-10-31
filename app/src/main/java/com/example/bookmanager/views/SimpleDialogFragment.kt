package com.example.bookmanager.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * ネガティブ／ポジティブボタンを実装したシンプルなアラートを生成するためのフラグメント。
 */
class SimpleDialogFragment : DialogFragment() {

    private var title: String = ""
    private var message: String = ""
    private var positiveButtonLabel: String = ""
    private var negativeButtonLabel: String = ""
    private var positiveButtonClickListener: DialogInterface.OnClickListener? = null
    private var negativeButtonClickListener: DialogInterface.OnClickListener? = null

    fun setTitle(title: String) {
        this.title = title
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setPositiveButton(label: String, listener: DialogInterface.OnClickListener?) {
        positiveButtonLabel = label
        positiveButtonClickListener = listener
    }

    fun setNegativeButton(label: String, listener: DialogInterface.OnClickListener?) {
        negativeButtonLabel = label
        negativeButtonClickListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity ?: throw IllegalStateException("Activity cannot be null.")
        val builder = AlertDialog.Builder(activity)
        return builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonLabel, positiveButtonClickListener)
            .setNegativeButton(negativeButtonLabel, negativeButtonClickListener)
            .create()
    }
}
