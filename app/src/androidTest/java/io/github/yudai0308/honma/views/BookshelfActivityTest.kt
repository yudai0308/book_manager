package io.github.yudai0308.honma.views


import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import io.github.yudai0308.honma.R
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
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

    // FIXME: テスト開始前に DB 内のデータをリセットする必要がある
    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun bookshelfActivityTest() {
        val view = onView(withId(R.id.fab_add_book))
        view.perform(click())

        val searchAutoComplete = onView(withResourceName("search_src_text"))
        searchAutoComplete.perform(replaceText("村上春樹"), closeSoftKeyboard())

        val searchAutoComplete2 = onView(withResourceName("search_src_text"))
        searchAutoComplete2.perform(pressImeActionButton())

        val searchResultCond = Until.hasObject(
            By.res(
                "io.github.yudai0308.honma", "book_search_item_image_card"
            )
        )
        val waitSuccess1 = device.wait(searchResultCond, 3000L)
        assertThat(waitSuccess1, `is`(true))

        onView(withId(R.id.book_search_result_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<BookshelfAdapter.BookShelfHolder>(
                0, clickChildViewWithId(R.id.book_search_add_button)
            )
        )

        val backButton = onView(
            allOf(
                withClassName(`is`(AppCompatImageButton::class.java.name)),
                withParent(withClassName(`is`(Toolbar::class.java.name)))
            )
        )
        backButton.perform(click())

        val bookshelfCond = Until.hasObject(
            By.res(
                "io.github.yudai0308.honma", "bookshelf_item_cover"
            )
        )
        val waitSuccess2 = device.wait(bookshelfCond, 3000L)
        assertThat(waitSuccess2, `is`(true))

        val imageView = onView(withId(R.id.bookshelf_item_cover))
        imageView.check(matches(isDisplayed()))
        doesNotExist()
    }

    private fun clickChildViewWithId(@IdRes id: Int) = object : ViewAction {
        override fun getConstraints(): Matcher<View>? = null

        override fun getDescription(): String {
            return "Click on a child view with specified id."
        }

        override fun perform(uiController: UiController?, view: View?) {
            view?.findViewById<View>(id)?.also {
                it.performClick()
            }
        }
    }
}
