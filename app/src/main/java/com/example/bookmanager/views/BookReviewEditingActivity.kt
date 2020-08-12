package com.example.bookmanager.views

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookReviewEditingBinding
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.Libs
import com.example.bookmanager.viewmodels.BookReviewViewModel

class BookReviewEditingActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookReviewEditingBinding>(
            this,
            R.layout.activity_book_review_editing
        )
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(BookReviewViewModel::class.java)
    }

    private val bookId by lazy { intent.getStringExtra(C.BOOK_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_review_editing)

        initToolbar()

        binding.also {
            it.viewModel = viewModel.apply {
                updateReviewContent(bookId)
            }
            it.lifecycleOwner = this
        }

        // TODO: テキストの初期値を取得しておき、戻るボタン押下時のテキストと差分があればダイアログを表示。

    }

    private fun initToolbar() {
        // as Toolbar がないとエラーになる。
        setSupportActionBar(binding.toolbar as Toolbar)

        // ツールバーに戻るボタンを表示。
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun saveReviewContent() {
        val text = binding.bookReviewEditor.text.toString()
        FileIO.saveReviewFile(this, bookId, text)
    }

    private fun createCancelAlertFragment(): SimpleDialogFragment {
        return SimpleDialogFragment().apply {
            val activity = this@BookReviewEditingActivity
            setTitle(activity.getString(R.string.cancel))
            setMessage(activity.getString(R.string.dialog_discard_input_content))
            setPositiveButton(
                activity.getString(R.string.yes),
                DialogInterface.OnClickListener { _, _ -> finish() }
            )
            setNegativeButton(activity.getString(R.string.cancel), null)
        }
    }

    /**
     * バックボタンが押されたとき、変更内容が破棄される旨をアラート表示する。
     */
    override fun onBackPressed() {
        createCancelAlertFragment().show(supportFragmentManager, C.DIALOG_TAG_CANCEL_EDITING)
        super.onBackPressed()
    }

    /**
     * ツールバーの戻るボタンが押されたとき、変更内容が破棄される旨をアラート表示する。
     */
    override fun onSupportNavigateUp(): Boolean {
        createCancelAlertFragment().show(supportFragmentManager, C.DIALOG_TAG_CANCEL_EDITING)
        return super.onSupportNavigateUp()
    }

    /**
     * レビュー内容を保存する。
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_save -> {
                saveReviewContent()
                finish()
                Libs.showToastLong(this, getString(R.string.saved))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * ツールバーに保存ボタンを追加する。
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
