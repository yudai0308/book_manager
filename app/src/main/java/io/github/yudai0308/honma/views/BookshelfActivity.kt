package io.github.yudai0308.honma.views

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.RadioButton
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.github.yudai0308.honma.R
import io.github.yudai0308.honma.databinding.ActivityBookshelfBinding
import io.github.yudai0308.honma.models.BookSortCondition
import io.github.yudai0308.honma.rooms.entities.Book
import io.github.yudai0308.honma.utils.C
import io.github.yudai0308.honma.utils.FileIO
import io.github.yudai0308.honma.viewmodels.BookshelfViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 本棚ページのアクティビティ。
 */
class BookshelfActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(BookshelfViewModel::class.java)
    }

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBookshelfBinding>(this, R.layout.activity_bookshelf)
    }

    private val sharedPref by lazy { getPreferences(Context.MODE_PRIVATE) }

    private var selectedBook: View? = null

    private lateinit var selectedFilterButton: Button

    private lateinit var selectedSortButton: RadioButton

    private var sortViewIsShown = false

    private var selectedFilter: Book.Status? = null

    private lateinit var selectedSort: BookSortCondition

    companion object {
        const val SORT_COND_COLUMN = "sort_cond_column"
        const val SORT_COND_IS_ASC = "sort_cond_is_asc"
    }

    private enum class BookMenu(val id: Int, val title: Int) {
        MANAGE(0, R.string.manage),
        DETAIL(1, R.string.detail),
        DELETE(2, R.string.delete)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase の初期化
        FirebaseApp.initializeApp(applicationContext)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)

        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }

        selectedSort = getSavedSortCondition()
        selectedSortButton = getSortButtonByCondition(selectedSort).apply {
            isChecked = true
        }
        selectedFilterButton = binding.filterButtons.filterButtonAll

        initToolbar()
        initRecyclerView()
        setFabClickListener()
        setFilterButtonsClickListener()
        setSortButtonsClickListener()

        binding.sortView.sortViewCloseButton.setOnClickListener {
            closeSortView()
        }
        binding.surfaceView.setOnClickListener {
            closeSortView()
        }
    }

    override fun onStart() {
        super.onStart()

        showBooksAccordingToSelectedCondition()
    }

    private fun getSortButtonByCondition(condition: BookSortCondition): RadioButton {
        return when (condition.column) {
            Book.Column.TITLE -> {
                if (condition.isAsc) {
                    binding.sortView.sortViewTitleAscRadioButton
                } else {
                    binding.sortView.sortViewTitleDescRadioButton
                }
            }
            Book.Column.AUTHOR -> {
                if (condition.isAsc) {
                    binding.sortView.sortViewAuthorAscRadioButton
                } else {
                    binding.sortView.sortViewAuthorDescRadioButton
                }
            }
            Book.Column.CREATED_AT -> {
                if (condition.isAsc) {
                    binding.sortView.sortViewAddedAtAscRadioButton
                } else {
                    binding.sortView.sortViewAddedAtDescRadioButton
                }
            }
            Book.Column.RATING -> {
                if (condition.isAsc) {
                    binding.sortView.sortViewMyRatingAscRadioButton
                } else {
                    binding.sortView.sortViewMyRatingDescRadioButton
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.toolbar.apply {
            setTitle(R.string.toolbar_title_bookshelf)
        } as Toolbar
        setSupportActionBar(toolbar)
    }

    private fun initRecyclerView() {
        val listener = View.OnClickListener {
            // クリックされた ImageView / TextView の２つ親の FrameLayout を渡す必要がある。
            val rootView = it.parent.parent as View
            val position = binding.bookshelfBookList.getChildAdapterPosition(rootView)
            viewModel.books.value?.get(position)?.let { book ->  startBookDetailActivity(book) }
        }

        val adapter = BookshelfAdapter().apply {
            setOnClickListener(listener)
            setOnBindViewHolderListener(object : BookshelfAdapter.OnBindViewHolderListener {
                override fun onBound(view: View) {
                    registerForContextMenu(view)
                }
            })
        }

        val spanCount = resources.getInteger(R.integer.bookshelf_grid_span_count)
        val manager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)

        binding.bookshelfBookList.also {
            it.layoutManager = manager
            it.adapter = adapter
            it.setHasFixedSize(true)
        }
    }

    private fun setFabClickListener() {
        binding.fabAddBook.setOnClickListener {
            val intent = Intent(applicationContext, BookSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setFilterButtonsClickListener() {
        val buttons = binding.filterButtons.run {
            listOf(
                filterButtonAll,
                filterButtonWantToRead,
                filterButtonReading,
                filterButtonFinished
            )
        }
        buttons.forEach { button ->
            button.setOnClickListener {
                val clickedButton = it as Button
                switchFilterButtonBackground(clickedButton)
                updateSelectedFilter(clickedButton)
                showBooksAccordingToSelectedCondition()
            }
        }
    }

    private fun getSavedSortCondition(): BookSortCondition {
        val columnCode = sharedPref.getInt(SORT_COND_COLUMN, Book.Column.CREATED_AT.code)
        val column = Book.Column.getByCode(columnCode) ?: Book.Column.CREATED_AT
        val isAsc = sharedPref.getBoolean(SORT_COND_IS_ASC, false)
        return BookSortCondition(column, isAsc)
    }

    private fun switchFilterButtonBackground(clickedButton: Button) {
        if (clickedButton.id == selectedFilterButton.id) {
            return
        }

        val selectedBackground = ContextCompat.getDrawable(this, R.drawable.bg_filter_btn_selected)
        val selectableBackground = ContextCompat.getDrawable(this, R.drawable.bg_filter_btn_selectable)
        selectedFilterButton.background = selectableBackground
        selectedFilterButton = clickedButton
        clickedButton.background = selectedBackground
    }

    private fun updateSelectedFilter(filterButton: Button) {
        selectedFilter = when (filterButton.id) {
            R.id.filter_button_all -> null
            R.id.filter_button_want_to_read -> Book.Status.WANT_TO_READ
            R.id.filter_button_reading -> Book.Status.READING
            R.id.filter_button_finished -> Book.Status.FINISHED
            else -> null
        }
    }

    private fun showBooksAccordingToSelectedCondition() {
        val books = runBlocking { viewModel.fetchBooks(selectedFilter, selectedSort) }
        if (books.isNullOrEmpty()) {
            binding.noBookText.text = getNoBooksText()
            binding.noBookText.visibility = View.VISIBLE
        } else {
            binding.noBookText.text = ""
            binding.noBookText.visibility = View.GONE
        }
    }

    private fun getNoBooksText(): String {
        return when (selectedFilter) {
            Book.Status.WANT_TO_READ -> getString(R.string.no_books_want_to_read_on_bookshelf)
            Book.Status.READING -> getString(R.string.no_books_reading_on_bookshelf)
            Book.Status.FINISHED -> getString(R.string.no_books_finished_on_bookshelf)
            else -> getString(R.string.no_books_on_bookshelf)
        }
    }

    private fun startBookDetailActivity(book: Book) {
        startActivity(Intent(applicationContext, BookDetailActivity::class.java).apply {
            putExtra(C.BOOK_ID, book.id)
        })
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu?.let {
            it.add(Menu.NONE, BookMenu.MANAGE.id, Menu.NONE, BookMenu.MANAGE.title)
            it.add(Menu.NONE, BookMenu.DETAIL.id, Menu.NONE, BookMenu.DETAIL.title)
            it.add(Menu.NONE, BookMenu.DELETE.id, Menu.NONE, BookMenu.DELETE.title)
        }

        view?.let {
            selectedBook = it.parent.parent as View
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = selectedBook?.let {
            binding.bookshelfBookList.getChildAdapterPosition(it)
        }
        val book = position?.let {
            viewModel.books.value?.get(it)
        } ?: return super.onContextItemSelected(item)

        when (item.itemId) {
            BookMenu.MANAGE.id -> {
                selectedBook?.let { startBookDetailActivity(book) }
            }
            BookMenu.DETAIL.id -> {
                val uri = Uri.parse(book.infoLink)
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
            BookMenu.DELETE.id -> {
                val dialog = createDeleteConfirmationDialog(book)
                dialog.show(supportFragmentManager, C.DIALOG_TAG_DELETE_BOOK)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun createDeleteConfirmationDialog(book: Book): SimpleDialogFragment {
        return SimpleDialogFragment().also {
            it.setTitle(book.title)
            it.setMessage(getString(R.string.delete_dialog_message))
            it.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                runBlocking {
                    viewModel.delete(book)
                    FileIO.deleteBookImage(this@BookshelfActivity, book.id)
                    showBooksAccordingToSelectedCondition()
                }
            })
            it.setNegativeButton(getString(R.string.cancel), null)
        }
    }

    private fun showSortViewWithSlideAnim() {
        val height = getSortViewHeight().toFloat()
        ObjectAnimator.ofFloat(binding.bookshelfSortView, "translationY", height * -1).apply {
            duration = 300
            start()
        }
    }

    private fun hideSortViewWithSlideAnim() {
        val height = getSortViewHeight().toFloat()
        ObjectAnimator.ofFloat(binding.bookshelfSortView, "translationY", height).apply {
            duration = 300
            start()
        }
    }

    private fun getSortViewHeight(): Int {
        return binding.bookshelfSortView.height
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bookshelf_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_sort -> {
                binding.surfaceView.isClickable = true
                sortViewIsShown = true
                showSortViewWithSlideAnim()
                fadeInSurfaceView()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun closeSortView() {
        binding.surfaceView.isClickable = false
        sortViewIsShown = false
        hideSortViewWithSlideAnim()
        fadeOutSurfaceView()
    }

    private fun fadeInSurfaceView() {
        binding.surfaceView.apply {
            visibility = View.VISIBLE
            startAnimation(AlphaAnimation(0.0F, 0.7F).apply {
                duration = 300
                fillAfter = true
            })
        }
    }

    private fun fadeOutSurfaceView() {
        binding.surfaceView.apply {
            startAnimation(AlphaAnimation(0.7F, 0.0F).apply {
                duration = 300
                fillAfter = true
            })
            visibility = View.GONE
        }
    }

    private fun setSortButtonsClickListener() {
        getSortButtons().forEach { button ->
            button.setOnCheckedChangeListener { compoundButton, _ ->
                if (compoundButton.id == selectedSortButton.id) {
                    return@setOnCheckedChangeListener
                }

                selectedSortButton.isChecked = false
                selectedSortButton = compoundButton as RadioButton
                sortBooks(selectedSortButton.id)
                savePreferences()
                Handler().postDelayed({
                    closeSortView()
                }, 500)
            }
        }
    }

    private fun sortBooks(@IdRes radioButtonId: Int) {
        when (radioButtonId) {
            binding.sortView.sortViewTitleAscRadioButton.id -> {
                selectedSort.column = Book.Column.TITLE
                selectedSort.isAsc = true
            }
            binding.sortView.sortViewTitleDescRadioButton.id -> {
                selectedSort.column = Book.Column.TITLE
                selectedSort.isAsc = false
            }
            binding.sortView.sortViewAuthorAscRadioButton.id -> {
                selectedSort.column = Book.Column.AUTHOR
                selectedSort.isAsc = true
            }
            binding.sortView.sortViewAuthorDescRadioButton.id -> {
                selectedSort.column = Book.Column.AUTHOR
                selectedSort.isAsc = false
            }
            binding.sortView.sortViewAddedAtAscRadioButton.id -> {
                selectedSort.column = Book.Column.CREATED_AT
                selectedSort.isAsc = true
            }
            binding.sortView.sortViewAddedAtDescRadioButton.id -> {
                selectedSort.column = Book.Column.CREATED_AT
                selectedSort.isAsc = false
            }
            binding.sortView.sortViewMyRatingAscRadioButton.id -> {
                selectedSort.column = Book.Column.RATING
                selectedSort.isAsc = true
            }
            binding.sortView.sortViewMyRatingDescRadioButton.id -> {
                selectedSort.column = Book.Column.RATING
                selectedSort.isAsc = false
            }
        }

        GlobalScope.launch { viewModel.fetchBooks(selectedFilter, selectedSort) }
    }

    private fun getSortButtons(): List<RadioButton> {
        return listOf(
            binding.sortView.sortViewTitleAscRadioButton,
            binding.sortView.sortViewTitleDescRadioButton,
            binding.sortView.sortViewAuthorAscRadioButton,
            binding.sortView.sortViewAuthorDescRadioButton,
            binding.sortView.sortViewAddedAtAscRadioButton,
            binding.sortView.sortViewAddedAtDescRadioButton,
            binding.sortView.sortViewMyRatingAscRadioButton,
            binding.sortView.sortViewMyRatingDescRadioButton
        )
    }

    private fun savePreferences() {
        sharedPref.edit {
            putInt(SORT_COND_COLUMN, selectedSort.column.code)
            putBoolean(SORT_COND_IS_ASC, selectedSort.isAsc)
            apply()
        }
    }

    override fun onBackPressed() {
        if (sortViewIsShown) {
            closeSortView()
        } else {
            super.onBackPressed()
        }
    }
}
