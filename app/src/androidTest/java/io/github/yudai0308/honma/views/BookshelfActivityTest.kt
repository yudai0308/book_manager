package io.github.yudai0308.honma.views


import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.github.yudai0308.honma.pageobjects.BookSearchPage
import io.github.yudai0308.honma.pageobjects.BookshelfPage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BookshelfActivityTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<BookshelfActivity>()

    private lateinit var device: UiDevice

    private lateinit var bookshelfPage: BookshelfPage

    private lateinit var bookSearchPage: BookSearchPage

    // FIXME: テスト開始前に DB 内のデータをリセットする必要がある
    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        bookshelfPage = BookshelfPage()
        bookSearchPage = BookSearchPage()
    }

    @Test
    fun 本を本棚に追加できる() {
        bookshelfPage.assertNoBookExists()
            .goBookSearchPage()
            .search("ハリーポッター")
            .waitForSearchCompleted(device)
            .clickAddButtonAtFirstPosition()
            .goBackBookshelfPage()
            .waitForBooksLoaded(device)
            .assertAnyBookExists()
    }
}
