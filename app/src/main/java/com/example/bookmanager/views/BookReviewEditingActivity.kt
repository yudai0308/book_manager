package com.example.bookmanager.views

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.bookmanager.utils.ViewUtil
import com.example.bookmanager.viewmodels.BookInfoViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher

/**
 * レビュー入力ページのアクティビティ。
 */
class BookReviewEditingActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookReviewEditingBinding>(
            this, R.layout.activity_book_review_editing
        )
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this, BookInfoViewModel.Factory(application, bookId)
        ).get(BookInfoViewModel::class.java)
    }

    private val bookId by lazy { intent.getStringExtra(C.BOOK_ID) }

    /**
     * レビューの初期値。
     * 保存せずに戻ろうとしたときに、アラートを出すか判断するために使う。
     */
    private lateinit var initialContent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_review_editing)

        initToolbar()
        initEditor()

        binding.also {
            it.viewModel = viewModel.apply {
                readReviewContent(bookId)
            }
            it.lifecycleOwner = this
        }

        // レビュー内容の初期値を保存。
        initialContent = viewModel.review
    }

    private fun initToolbar() {
        // as Toolbar がないとエラーになる。
        setSupportActionBar(binding.toolbar as Toolbar)

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_edit_review)
            // ツールバーに戻るボタンを表示。
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initEditor() {
        val markwon = Markwon.create(this)
        val editor = MarkwonEditor.create(markwon)
        binding.bookReviewEditor.apply {
            // マークダウン用の TextWatcher。
            addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
            // ViewModel の値をリアルタイムで変更するための TextWatcher。
            addTextChangedListener(ReviewTextWatcher())
        }
    }

    /**
     * 感想を内部ストレージに保存する。
     */
    private fun saveReviewContent() {
        val text = viewModel.review
        FileIO.saveReviewFile(this, bookId, text)
    }

    /**
     * 画面を戻ろうとした際のアラートダイアログを作成する。
     *
     * @return [SimpleDialogFragment] オブジェクト
     */
    private fun createCancelAlertFragment(): SimpleDialogFragment {
        return SimpleDialogFragment().apply {
            val activity = this@BookReviewEditingActivity
            setTitle(activity.getString(R.string.cancel))
            setMessage(activity.getString(R.string.dialog_discard_input_content))
            setPositiveButton(activity.getString(R.string.yes),
                DialogInterface.OnClickListener { _, _ -> finish() })
            setNegativeButton(activity.getString(R.string.cancel), null)
        }
    }

    /**
     * バックボタンが押されたとき、変更内容が破棄される旨をアラート表示する。
     */
    override fun onBackPressed() {
        if (contentChanged()) {
            createCancelAlertFragment().show(supportFragmentManager, C.DIALOG_TAG_CANCEL_EDITING)
        } else {
            finish()
        }
    }

    /**
     * ツールバーの戻るボタンが押されたとき、変更内容が破棄される旨をアラート表示する。
     */
    override fun onSupportNavigateUp(): Boolean {
        if (contentChanged()) {
            createCancelAlertFragment().show(supportFragmentManager, C.DIALOG_TAG_CANCEL_EDITING)
        } else {
            finish()
        }
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 「保存」ボタンタップで感想を保存する。
            R.id.toolbar_save -> {
                saveReviewContent()
                finish()
                ViewUtil.showToastLong(this, getString(R.string.saved))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // ツールバーに保存ボタンを追加する。
        menuInflater.inflate(R.menu.book_editor_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * レビューが編集されたかどうかを確認する。
     * 画面を閉じるときにアラートを表示するか判断するために使う。
     */
    private fun contentChanged(): Boolean {
        return initialContent != viewModel.review
    }

    /**
     * ユーザーの入力値をリアルタイムで ViewModel に保存するための [TextWatcher]。
     */
    inner class ReviewTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.review = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}
