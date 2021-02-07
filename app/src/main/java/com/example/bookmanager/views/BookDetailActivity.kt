package com.example.bookmanager.views

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookDetailBinding
import com.example.bookmanager.rooms.common.BookRepository
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.ImageUtil
import com.example.bookmanager.utils.StringUtil
import com.example.bookmanager.viewmodels.BookInfoViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * 本詳細ページのアクティビティ。
 */
class BookDetailActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookDetailBinding>(
            this, R.layout.activity_book_detail
        )
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this, BookInfoViewModel.Factory(application, bookId)
        ).get(BookInfoViewModel::class.java)
    }

    private val bookId by lazy {
        intent.getStringExtra(C.BOOK_ID)
    }

    private val bookDao by lazy {
        Room.databaseBuilder(this, BookDatabase::class.java, C.DB_NAME).build().bookDao()
    }

    private val bookInfo by lazy {
        runBlocking { bookDao.loadBookInfoById(bookId) }
    }

    private val handler = Handler()

    /**
     * 本の紹介テキストを短く表示している場合は true。
     */
    private var isShortText = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        initToolbar()
        createViewPagerContents()
        showAverageRating(bookId)
    }

    override fun onResume() {
        super.onResume()

        val bookTitle = bookInfo.book.title
        val authors = bookInfo.authors.map { it.name }
        val authorsString = StringUtil.listToString(authors)
        val bookImage = runBlocking {
            val context = this@BookDetailActivity
            val image = FileIO.readBookImage(context, bookId)
            image ?: ContextCompat.getDrawable(context, R.drawable.no_image)
        }

        binding.bookBasicInfo.also {
            it.bookDetailTitle.text = bookTitle
            it.bookDetailAuthor.text = authorsString
            it.bookDetailImage.setImageDrawable(bookImage)
        }

        initBookDescription()
    }

    private fun initToolbar() {
        // as Toolbar がないとエラーになる。
        setSupportActionBar(binding.toolbar as Toolbar)

        supportActionBar?.apply {
            title = getString(R.string.toolbar_title_book_detail)
            // ツールバーに戻るボタンを表示。
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initBookDescription() {
        val binding = binding.bookBasicInfo
        binding.bookDescSummaryShort.also { shortText ->
            shortText.text = viewModel.getDescription()
            shortText.setOnClickListener {
                isShortText = false
                it.visibility = View.GONE
                binding.bookDescSummaryAll.visibility = View.VISIBLE
            }
        }
        binding.bookDescSummaryAll.also { allText ->
            allText.text = viewModel.getDescription()
            allText.setOnClickListener {
                isShortText = true
                it.visibility = View.GONE
                binding.bookDescSummaryShort.visibility = View.VISIBLE
            }
        }
    }

    /**
     * ViewPager 内のコンテンツ「メモ」と「感想」を作成する。
     */
    private fun createViewPagerContents() {
        val viewPager = createViewPager()
        val mediator = createTabLayoutMediator(viewPager)
        mediator.attach()
    }

    /**
     * 「詳細」と「感想」画面で構成される [ViewPager2] を作成する。
     *
     * @return [ViewPager2] オブジェクト
     */
    private fun createViewPager(): ViewPager2 {
        val bookMemoFragment = BookMemoFragment.getInstance(bookId)
        val bookReviewFragment = BookReviewFragment.getInstance(bookId)
        return binding.bookDetailViewPager.also {
            it.isUserInputEnabled = false
            it.adapter = BookDetailPagerAdapter(
                this, bookMemoFragment, bookReviewFragment
            )
        }
    }

    /**
     * 「詳細」と「感想」タブを生成するための [TabLayoutMediator] を作成する。
     *
     * @param viewPager 「詳細」と「感想」画面で構成された [ViewPager2]
     * @return [TabLayoutMediator] オブジェクト
     */
    private fun createTabLayoutMediator(viewPager: ViewPager2): TabLayoutMediator {
        val tabLayout = binding.bookDetailTabLayout
        return TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                BookDetailPagerAdapter.Tab.BOOK_MEMO.position -> {
                    getString(R.string.book_detail_tab_memo)
                }
                BookDetailPagerAdapter.Tab.BOOK_REVIEW.position -> {
                    getString(R.string.book_detail_tab_review)
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    /**
     * レビューのレートを表示させる。
     *
     * @param bookId 本 ID
     */
    private fun showAverageRating(bookId: String) {
        val url = C.BOOK_SEARCH_API_URL + "/" + bookId
        val req = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder().build()
        val call = client.newCall(req)
        call.enqueue(createFetchAverageRatingCallback())
    }

    /**
     * API から本の情報を取得してレートを表示させるコールバック。
     */
    private fun createFetchAverageRatingCallback() = object : Callback {
        override fun onFailure(call: Call, e: IOException) {}

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            body ?: return
            val jsonObj = JSONObject(body)
            val volumeInfo = jsonObj.getJSONObject("volumeInfo")
            if (volumeInfo.has("averageRating")) {
                val averageRating = volumeInfo.getString("averageRating")
                handler.post {
                    binding.bookBasicInfo.bookDetailRatingBar.rating = averageRating.toFloat()
                }
            }
            if (volumeInfo.has("ratingsCount")) {
                val ratingsCount = volumeInfo.getString("ratingsCount")
                handler.post {
                    binding.bookBasicInfo.bookDetailRatingsCount.text = ratingsCount
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // ツールバーに「感想を書く」ボタンを追加。
        menuInflater.inflate(R.menu.book_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 感想を書く
            R.id.toolbar_edit_review -> {
                startActivity(Intent(applicationContext, BookReviewEditingActivity::class.java).apply {
                    putExtra(C.BOOK_ID, bookId)
                })
            }
            // 本の表紙を変更する
            R.id.toolbar_change_book_image -> {
                pickImage()
            }
            // 本を削除する
            R.id.toolbar_delete_book -> {
                val book = runBlocking { bookDao.load(bookId) }
                showDeleteConfirmationDialog(book)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pickImage() {
        ImagePicker.with(this)
            .crop(100F, 150F)
            .compress(C.IMAGE_MAX_SIZE)
            .start { resultCode, data ->
                if (resultCode != Activity.RESULT_OK) {
                    return@start
                }

                val uri = data?.data ?: return@start
                Glide.with(this).load(uri).into(binding.bookBasicInfo.bookDetailImage)
                val bitmap = ImageUtil.getBitmapWithUri(this, uri)
                FileIO.saveBookImage(this, bitmap, bookId)

        }
    }

    private fun showDeleteConfirmationDialog(book: Book) {
        SimpleDialogFragment().also {
            it.setTitle(book.title)
            it.setMessage(getString(R.string.delete_dialog_message))
            it.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                val daoController = BookRepository(this)
                runBlocking {
                    daoController.deleteBook(book)
                    FileIO.deleteBookImage(this@BookDetailActivity, book.id)
                }
                finish()
            })
            it.setNegativeButton(getString(R.string.cancel), null)
        }.show(supportFragmentManager, C.DIALOG_TAG_DELETE_BOOK)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
