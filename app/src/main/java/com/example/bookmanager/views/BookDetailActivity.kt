package com.example.bookmanager.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.bookmanager.R
import com.example.bookmanager.databinding.ActivityBookDetailBinding
import com.example.bookmanager.rooms.database.BookDatabase
import com.example.bookmanager.rooms.entities.BookInfo
import com.example.bookmanager.utils.C
import com.example.bookmanager.utils.FileIO
import com.example.bookmanager.utils.Libs
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

    private val bookId by lazy { intent.getStringExtra(C.BOOK_ID) }

    private val bookInfo by lazy { loadBookInfo(bookId) }

    private val bookImage by lazy {
        runBlocking {
            FileIO.readBookImage(this@BookDetailActivity, bookId)
        }
    }

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        initToolbar()
        createMainContents()
        showAverageRating(bookId)
    }

    override fun onResume() {
        super.onResume()

        val bookTitle = bookInfo?.book?.title ?: ""
        val authors = bookInfo?.authors?.map { it.name }
        val authorsString =
            authors?.let { Libs.listToString(it) } ?: getString(R.string.unknown_author)
        binding.apply {
            bookDetailTitle.text = bookTitle
            bookDetailAuthor.text = authorsString
            bookDetailImage.setImageDrawable(bookImage)
        }
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

    /**
     * メインコンテンツの「詳細」と「感想」を作成する。
     */
    private fun createMainContents() {
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
        val description = getBookDescription(bookInfo)
        val bookDescriptionFragment = BookDescriptionFragment.newInstance(description)
        val bookReviewFragment = BookReviewFragment.newInstance(bookId)
        return binding.bookDetailViewPager.apply {
            adapter = BookDetailPagerAdapter(
                this@BookDetailActivity, bookDescriptionFragment, bookReviewFragment
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
                BookDetailPagerAdapter.BookDetailPage.BOOK_DESCRIPTION.position -> {
                    getString(R.string.book_detail_tab_description)
                }
                BookDetailPagerAdapter.BookDetailPage.BOOK_REVIEW.position -> {
                    getString(R.string.book_detail_tab_review)
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    /**
     * ローカルデータベースから本の情報を取得する。
     *
     * @param bookId 本のID
     * @return [BookInfo] オブジェクト
     */
    private fun loadBookInfo(bookId: String): BookInfo? {
        val db = Room.databaseBuilder(this, BookDatabase::class.java, C.DB_NAME).build()
        val bookDao = db.bookDao()
        return runBlocking { bookDao.loadBookInfoById(bookId) }
    }

    /**
     * [BookInfo] から本の説明を取得する。
     *
     * @param bookInfo [BookInfo] オブジェクト
     * @return 本の説明
     */
    private fun getBookDescription(bookInfo: BookInfo?): String {
        val description = bookInfo?.book?.description
        return if (description.isNullOrBlank()) {
            getString(R.string.book_description_not_found)
        } else {
            description
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
        call.enqueue(FetchAverageRatingCallback())
    }

    /**
     * API から本の情報を取得してレートを表示させるコールバッククラス。
     */
    inner class FetchAverageRatingCallback : Callback {
        override fun onFailure(call: Call, e: IOException) {}

        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            body ?: return
            val jsonObj = JSONObject(body)
            val volumeInfo = jsonObj.getJSONObject("volumeInfo")
            if (volumeInfo.has("averageRating")) {
                val averageRating = volumeInfo.getString("averageRating")
                handler.post {
                    binding.bookDetailRatingBar.rating = averageRating.toFloat()
                }
            }
            if (volumeInfo.has("ratingsCount")) {
                val ratingsCount = volumeInfo.getString("ratingsCount")
                handler.post {
                    binding.bookDetailRatingsCount.text = ratingsCount
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
            // 「感想を書く」をタップで画面遷移。
            R.id.toolbar_edit_review -> {
                startActivity(Intent(
                    applicationContext, BookReviewEditingActivity::class.java
                ).apply {
                    putExtra(C.BOOK_ID, bookId)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
